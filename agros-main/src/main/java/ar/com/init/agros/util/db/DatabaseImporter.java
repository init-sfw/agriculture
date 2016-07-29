package ar.com.init.agros.util.db;

import ar.com.init.agros.controller.util.EntityManagerUtil;
import ar.com.init.agros.model.servicio.TipoServicio;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author gmatheu
 */
public class DatabaseImporter {

    public static final String SCRIPT_FILE = "script.sql";
    public static final String SENTENCE_DELIMITER = ";";
    private static final Logger logger = Logger.getLogger(DatabaseImporter.class.getName());

    static {
        try {
            FileHandler importHandler = new FileHandler("import.log", true);
            importHandler.setFormatter(new SimpleFormatter());
            importHandler.setLevel(Level.ALL);
            logger.addHandler(importHandler);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    private File file;
    private Connection tempDatabaseConnection;
    private EntityManager em;
    private Map<String, TableProcessor> toProcessTables;
    private Queue<TableProcessor> toProcessTablesQueue;
    private Pattern insertPattern = Pattern.compile("INSERT .* VALUES");
    private String insertStart = "INSERT INTO PUBLIC\\.";
    private Pattern tablePattern = Pattern.compile(insertStart + ".*(?=\\()");
    private Pattern uuidPattern = Pattern.compile("[\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12}");

    public DatabaseImporter(String path) {
        file = new File(path);
        em = EntityManagerUtil.createEntityManager();

        toProcessTables = new HashMap<String, TableProcessor>();
        toProcessTablesQueue = new LinkedList<TableProcessor>();
        addTablesToProccess();
    }

    public void importData() throws Exception {
        String v = VersionChecker.checkVersion(file);

        File oldDB = new File("oldDB.zip");
        ar.com.init.agros.db.h2.BackupTool.backup(oldDB.getAbsolutePath());

        logger.info(String.format("Importing %s database", v));
        processFile();
    }

    private void processFile() throws Exception {

        logger.info("Decompressing file...");

        try {
            ZipFile zipFile = new ZipFile(file);
            ZipEntry entry = zipFile.getEntry(SCRIPT_FILE);

            InputStream is = zipFile.getInputStream(entry);
            createTempDatabase(is);
            is.close();

            is = zipFile.getInputStream(entry);
            em.getTransaction().begin();
            process(is);
            em.getTransaction().commit();
            is.close();

            zipFile.close();

        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        } finally {
            tempDatabaseConnection.close();
            em.close();
        }
    }

    private void process(InputStream is) throws Exception {
        Scanner scanner = new Scanner(is);
        scanner.useDelimiter(SENTENCE_DELIMITER);

        while (scanner.hasNext()) {
            String sentence = scanner.next();

            Matcher insertMatcher = insertPattern.matcher(sentence);

            if (insertMatcher.find()) {
                processInsert(sentence);

            } else {
                logger.fine(String.format("Discarding: %s", sentence));
            }
        }

        executeProcessors();
    }

    private void executeProcessors() {

        TableCrearerProcessor clearer = new TableCrearerProcessor();
        try {
            clearer.setup("", new String[]{});
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        clearer.procces();

        TableProcessor proc = null;
        while ((proc = toProcessTablesQueue.poll()) != null) {
            proc.procces();
        }
    }

    private void processInsert(String sentence) throws Exception {
        Scanner scanner = new Scanner(sentence);

        String insert = null;
        while (scanner.hasNextLine()) {
            insert = scanner.nextLine();
            Matcher insertLineMatcher = insertPattern.matcher(insert);
            if (insertLineMatcher.find()) {
                break;
            }
        }

        List<String> inserts = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            String values = insert.concat(scanner.nextLine());
            if (values.endsWith(",")) {
                values = values.substring(0, values.length() - 1) + SENTENCE_DELIMITER;
            }
            inserts.add(values);
        }

        Matcher tableMatcher = tablePattern.matcher(sentence);
        if (tableMatcher.find()) {
            String table = tableMatcher.group(0).replace(insertStart.replace("\\", ""), "");

            if (toProcessTables.containsKey(table)) {
                toProcessTables.get(table).setup(table, inserts.toArray(new String[0]));
            }
        } else {
            logger.fine(String.format("Table Not Found: %s", sentence));
        }
    }

    private void createTempDatabase(InputStream is) {
        try {
            Class.forName("org.h2.Driver");
            tempDatabaseConnection = DriverManager.getConnection("jdbc:h2:mem:osirisV10", "sa", "osiris123");

            Statement st = tempDatabaseConnection.createStatement();

            Scanner scanner = new Scanner(is);
            scanner.useDelimiter(SENTENCE_DELIMITER);

            while (scanner.hasNext()) {
                String sentence = scanner.next();
                st.addBatch(sentence);
            }

            st.executeBatch();

            logger.info("Temp database created");

        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    private void addToProccess(String table, TableProcessor processor, boolean clear) {
        toProcessTables.put(table, processor);
        toProcessTablesQueue.add(processor);
    }

    private void addTablesToProccess() {

        addToProccess("CONFIGURATION", new ConfigurationProcessor(), true);
        addToProccess("UNIDADMEDIDA", new StraighForwardInsertionProcessor(), true);

        addToProccess("PROVEEDOR", new ServicioProcessor(), true);
        addToProccess("TELEFONO", new StraighForwardInsertionProcessor(), true);
        addToProccess("PROVEEDOR_TELEFONO", new ServicioTelefonoProcessor(), true);

        addToProccess("CULTIVO", new StraighForwardInsertionProcessor(), true);
        addToProccess("VARIEDADCULTIVO", new VariedadCultivoProcessor(), true);

        addToProccess("INFORMACIONAGROQUIMICO", new StraighForwardInsertionProcessor(), true);
        addToProccess("ACCION", new StraighForwardInsertionProcessor(), true);
        addToProccess("INFORMACIONAGROQUIMICO_ACCION", new JoinTableProcessor("INFORMACIONAGROQUIMICO", "ACCION"), true);
        addToProccess("AGROQUIMICO", new StraighForwardInsertionProcessor(), true);

        addToProccess("DEPOSITO", new DepositoProcessor(), true);
        addToProccess("VALORDEPOSITO", new ValorDepositoProcessor(), true);

        addToProccess("SUPERFICIE", new SuperficieProcessor(), true);

        addToProccess("CAMPANIA", new StraighForwardInsertionProcessor(), true);

        addToProccess("BONIFICACION", new StraighForwardInsertionProcessor(), true);
        addToProccess("CAMPANIA_BONIFICACION", new JoinTableProcessor("CAMPANIA", "BONIFICACION"), true);

        addToProccess("TIPOINGRESO", new StraighForwardInsertionProcessor(), true);
        addToProccess("INGRESO", new StraighForwardInsertionProcessor(), true);

        addToProccess("TIPOCOSTO", new StraighForwardInsertionProcessor(), true);
        addToProccess("COSTO", new StraighForwardInsertionProcessor(), true);
        addToProccess("CAMPANIA_COSTO", new JoinTableProcessor("CAMPANIA", "COSTO"), true);
        addToProccess("CAMPANIA_INGRESO", new JoinTableProcessor("CAMPANIA", "INGRESO"), true);

        addToProccess("MOMENTOAPLICACION", new StraighForwardInsertionProcessor(), true);
        addToProccess("DETALLEPLANIFICACION", new StraighForwardInsertionProcessor(), true);
        addToProccess("DIVISA", new StraighForwardInsertionProcessor(), true);
        addToProccess("FORMAFUMIGACION", new StraighForwardInsertionProcessor(), true);

        addToProccess("MOVIMIENTOSTOCK", new StraighForwardInsertionProcessor(), true);
        addToProccess("DETALLEMOVIMIENTOSTOCK", new DetalleMovimientoStockProcessor(), true);
        addToProccess("MOVIMIENTOSTOCK_DETALLEMOVIMIENTOSTOCK", new JoinTableProcessor("MOVIMIENTOSTOCK", "DETALLEMOVIMIENTOSTOCK"), true);

        addToProccess("SEMILLA", new StraighForwardInsertionProcessor(), true);
        addToProccess("PLANIFICACIONAGROQUIMICO", new StraighForwardInsertionProcessor(), true);
        addToProccess("PLANIFICACIONAGROQUIMICO_DETALLEPLANIFICACION", new JoinTableProcessor("PLANIFICACIONAGROQUIMICO", "DETALLEPLANIFICACION"), true);
        addToProccess("PLANIFICACIONAGROQUIMICO_SUPERFICIE", new JoinTableProcessor("PLANIFICACIONAGROQUIMICO", "SUPERFICIE"), true);

        addToProccess("SIEMBRA", new StraighForwardInsertionProcessor(), true);
        addToProccess("RENDIMIENTOSUPERFICIE", new StraighForwardInsertionProcessor(), true);
        addToProccess("BONIFICACION_SIEMBRA", new JoinTableProcessor("BONIFICACION", "SIEMBRA"), true);
        addToProccess("SIEMBRA_COSTOS", new JoinTableProcessor("SIEMBRA", "COSTO"), true);
        addToProccess("SIEMBRA_COSTOSPOSTCOSECHA", new JoinTableProcessor("SIEMBRA", "COSTO"), true);
        addToProccess("SIEMBRA_SUPERFICIE", new JoinTableProcessor("SIEMBRA", "SUPERFICIE"), true);

        addToProccess("TRABAJO", new StraighForwardInsertionProcessor(), true);
        addToProccess("DETALLETRABAJO", new DetalleTrabajoProcessor(), true);
        addToProccess("TRABAJO_DETALLETRABAJO", new JoinTableProcessor("TRABAJO", "DETALLETRABAJO"), true);
        addToProccess("TRABAJO_COSTO", new JoinTableProcessor("TRABAJO", "COSTO"), true);
        addToProccess("TRABAJO_SUPERFICIE", new JoinTableProcessor("TRABAJO", "SUPERFICIE"), true);

        addToProccess("LLUVIA", new StraighForwardInsertionProcessor(), true);
        addToProccess("LLUVIA_SUPERFICIE", new JoinTableProcessor("LLUVIA", "SUPERFICIE"), true);

    }

    abstract class TableProcessor {

        List<String> queries;
        protected String tableName;
        final String sql = "DELETE %s ";
        protected List<String> tablesToClear;

        public TableProcessor() {
            queries = new ArrayList<String>();
            tablesToClear = new ArrayList<String>();
        }

        abstract void setup(String table, String[] inserts) throws Exception;

        protected void procces() {

            tablesToClear.add(tableName);
            clear();
            Pattern decimalPattern = Pattern.compile("(\\d),(\\d)");
            for (String query : queries) {
                Matcher matcher = decimalPattern.matcher(query);
                String effectiveQuery = matcher.replaceAll("$1.$2");
                effectiveQuery = effectiveQuery.replace("'null'", "NULL");

                em.createNativeQuery(effectiveQuery).executeUpdate();
                logger.fine(String.format("Executing: %s", query));
            }
        }

        protected void clear() {

            final String refIntegritySQL = "SET REFERENTIAL_INTEGRITY %s";
            em.createNativeQuery(String.format(refIntegritySQL, "FALSE")).executeUpdate();
            for (String t : tablesToClear) {
                if (t != null) {
                    Query qry = em.createNativeQuery(String.format(sql, t));
                    qry.executeUpdate();
                }
            }
            em.createNativeQuery(String.format(refIntegritySQL, "FALSE")).executeUpdate();
        }

        public String getTableName() {
            return tableName;
        }

        protected void insertQuery(String query) {
            if (!queries.contains(query)) {
                queries.add(query);
            }
        }
    }

    class TableCrearerProcessor extends TableProcessor {

        @Override
        void setup(String table, String[] inserts) throws Exception {
            tablesToClear.addAll(Arrays.asList("ACCION",
                    "AGROQUIMICO",
                    "ALMACENAMIENTO",
                    "BONIFICACION",
                    "BONIFICACION_SIEMBRA",
                    "CAMPANIA",
                    "CAMPANIA_BONIFICACION",
                    "CAMPANIA_COSTO",
                    "CAMPANIA_INGRESO",
                    "CONFIGURATION",
                    "COSTO",
                    "CULTIVO",
                    "DEPOSITO",
                    "DETALLECANCELACIONEGRESOCEREAL",
                    "DETALLECANCELACIONEGRESOSEMILLA",
                    "DETALLECANCELACIONINGRESOCEREAL",
                    "DETALLECANCELACIONINGRESOSEMILLA",
                    "DETALLEEGRESOCEREAL",
                    "DETALLEEGRESOSEMILLA",
                    "DETALLEINGRESOCEREAL",
                    "DETALLEINGRESOSEMILLA",
                    "DETALLEMOVIMIENTOSTOCK",
                    "DETALLEPLANIFICACION",
                    "DETALLETRABAJO",
                    "DIVISA",
                    "EMAILMESSAGE",
                    "FORMAFUMIGACION",
                    "INFORMACIONAGROQUIMICO",
                    "INFORMACIONAGROQUIMICO_ACCION",
                    "INGRESO",
                    "LLUVIA",
                    "LLUVIA_SUPERFICIE",
                    "MOMENTOAPLICACION",
                    "MOVIMIENTOGRANO",
                    "MOVIMIENTOGRANO_DETALLEMOVIMIENTOGRANO",
                    "MOVIMIENTOSTOCK",
                    "MOVIMIENTOSTOCK_DETALLEMOVIMIENTOSTOCK",
                    "PLANIFICACIONAGROQUIMICO",
                    "PLANIFICACIONAGROQUIMICO_DETALLEPLANIFICACION",
                    "PLANIFICACIONAGROQUIMICO_SUPERFICIE",
                    "RENDIMIENTOSUPERFICIE",
                    "SEMILLA",
                    "SERVICIO",
                    "SERVICIO_TELEFONO",
                    "SIEMBRA",
                    "SIEMBRA_COSTOS",
                    "SIEMBRA_COSTOSPOSTCOSECHA",
                    "SIEMBRA_SUPERFICIE",
                    "SILO",
                    "SUGERENCIA",
                    "SUPERFICIE",
                    "SUPERFICIE_ALMACENAMIENTO",
                    "TELEFONO",
                    "TIPOCOSTO",
                    "TIPOINGRESO",
                    "TIPOMOVIMIENTOSTOCK",
                    "TRABAJO",
                    "TRABAJO_COSTO",
                    "TRABAJO_DETALLETRABAJO",
                    "TRABAJO_SUPERFICIE",
                    "UNIDADMEDIDA",
                    "VALORAGROQUIMICO",
                    "VALORCEREAL",
                    "VALORGRANO",
                    "VALORSEMILLA",
                    "VARIEDADCULTIVO"));
        }
    }

    class StraighForwardInsertionProcessor extends TableProcessor {

        @Override
        public void setup(String table, String[] inserts) {

            this.tableName = table;

            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", table);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        insertQuery(insert);
                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class JoinTableProcessor extends TableProcessor {

        private String firstTable;
        private String joinTable;

        public JoinTableProcessor(String firstTable, String joinTable) {
            super();
            this.firstTable = firstTable;
            this.joinTable = joinTable;
        }

        @Override
        public void setup(String table, String[] inserts) throws Exception {

            tableName = table;
            String selectFirstById = String.format("SELECT COUNT(*) FROM %s WHERE ID = :id", firstTable);
            Query qryFirst = em.createNativeQuery(selectFirstById);

            String selectJoinById = String.format("SELECT COUNT(*) FROM %s WHERE ID = :id", joinTable);
            Query qryJoin = em.createNativeQuery(selectJoinById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String idF = matcher.group();
                    matcher.find();
                    String idJ = matcher.group();

                    qryFirst.setParameter("id", idF);
                    qryJoin.setParameter("id", idJ);
                    BigInteger countFirst = (BigInteger) qryFirst.getSingleResult();
                    BigInteger countJoin = (BigInteger) qryJoin.getSingleResult();

//                    if (countFirst.longValue() == 0 && countJoin.longValue() == 0) {
                    try {
                        insertQuery(insert);

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (Exception ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class ServicioProcessor extends TableProcessor {

        public static final String SERVICIO_TABLE = "SERVICIO";
        private static final String INSERT_SQL = "INSERT INTO PUBLIC.SERVICIO(ID, STATUS, VERSION, DOMICILIO, RAZONSOCIAL, TIPO) VALUES('%s','%s',%d,'%s','%s','%s');";

        @Override
        public void setup(String table, String[] inserts) throws Exception {

            tableName = SERVICIO_TABLE;
            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", SERVICIO_TABLE); // Se fija si en la db nueva hay una entidad con el mismo id y lo toma como el mismo
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        Statement st = tempDatabaseConnection.createStatement();
                        ResultSet rs = st.executeQuery(String.format("SELECT ID, STATUS, VERSION, DOMICILIO, RAZONSOCIAL FROM PROVEEDOR WHERE id = '%s'", id));

                        if (rs.next()) {
                            String newInsert = String.format(INSERT_SQL, id, rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), TipoServicio.PROVEEDOR_INSUMOS.id());
                            insertQuery(newInsert);
                        }

                        rs.close();

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (Exception ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class ServicioTelefonoProcessor extends TableProcessor {

        public static final String SERVICIO_TELEFONO_TABLE = "SERVICIO_TELEFONO";
        private static final String INSERT_SQL = "INSERT INTO PUBLIC.SERVICIO_TELEFONO VALUES('%s','%s');";

        @Override
        public void setup(String table, String[] inserts) throws Exception {

            tableName = SERVICIO_TELEFONO_TABLE;
            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE SERVICIO_ID = :idP AND TELEFONOS_ID = :idT", SERVICIO_TELEFONO_TABLE);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String idP = matcher.group();
                    matcher.find();
                    String idT = matcher.group();

                    qry.setParameter("idP", idP);
                    qry.setParameter("idT", idT);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        String newInsert = String.format(INSERT_SQL, idP, idT);
                        insertQuery(newInsert);

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (Exception ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class DepositoProcessor extends TableProcessor {

        private static final String ALMACENAMIENTO_TABLE = "ALMACENAMIENTO";
        private static final String DEPOSITO_TABLE = "DEPOSITO";
        private static final String DEPOSITO_INSERT = "INSERT INTO DEPOSITO(ID) VALUES ('%s')";

        @Override
        void setup(String table, String[] inserts) throws Exception {
            tableName = ALMACENAMIENTO_TABLE;
            tablesToClear.add(DEPOSITO_TABLE);

            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", ALMACENAMIENTO_TABLE);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        queries.add(insert.replace(table, ALMACENAMIENTO_TABLE));
                        insertQuery(String.format(DEPOSITO_INSERT, id));

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class ValorDepositoProcessor extends TableProcessor {

        private static final String VALOR_AGROQUIMICO_TABLE = "VALORAGROQUIMICO";

        @Override
        void setup(String table, String[] inserts) throws Exception {
            tableName = VALOR_AGROQUIMICO_TABLE;
            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", VALOR_AGROQUIMICO_TABLE);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        insertQuery(insert.replace(table, VALOR_AGROQUIMICO_TABLE).replace("DEPOSITO_ID", "ALMACENAMIENTO_ID"));

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class DetalleMovimientoStockProcessor extends TableProcessor {

        @Override
        void setup(String table, String[] inserts) throws Exception {
            tableName = table;
            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", table);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        insertQuery(insert.replace("VALORDEPOSITO_ID", "VALOR_ID"));

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class DetalleTrabajoProcessor extends TableProcessor {

        private static final String INSERT_SQL = "INSERT INTO PUBLIC.DETALLETRABAJO(ID, STATUS, VERSION, SUPERFICIEPLANIFICADA, AGUA, CANTIDADUTILIZADA, MONTOCOSTOHA, PERSISTIDO, AGROQUIMICO_ID, MOMENTOAPLICACION_ID, UNIDADAGUA_ID, UNIDADCANTIDADUTILIZADA_ID, DIVISACOSTOHA_ID, UNIDADCOSTOHA_ID, DEPOSITO_ID) "
                + " VALUES ('%s', '%s', %d, %.2f, %.2f, %.2f, %.4f, %s, '%s', '%s', '%s', '%s', '%s', '%s', '%s');";

        @Override
        void setup(String table, String[] inserts) throws Exception {
            tableName = table;
            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", table);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        Statement st = tempDatabaseConnection.createStatement();
                        ResultSet rs = st.executeQuery(String.format("SELECT DT.ID, DT.STATUS, DT.VERSION, DT.SUPERFICIEPLANIFICADA, DT.AGUA, "
                                + " DT.CANTIDADUTILIZADA, DT.MONTOCOSTOHA, DT.PERSISTIDO, DT.AGROQUIMICO_ID, DT.MOMENTOAPLICACION_ID, "
                                + " DT.UNIDADAGUA_ID, DT.UNIDADCANTIDADUTILIZADA_ID, DT.DIVISACOSTOHA_ID, DT.UNIDADCOSTOHA_ID, S.DEPOSITO_ID "
                                + " FROM DETALLETRABAJO AS DT, TRABAJO_DETALLETRABAJO AS TDT, TRABAJO AS T, SUPERFICIE AS S "
                                + " WHERE DT.ID = '%s' AND DT.ID = TDT.DETALLES_ID AND TDT.TRABAJO_ID = T.ID AND T.CAMPO_ID = S.ID", id));

                        if (rs.next()) {
                            String newInsert = String.format(INSERT_SQL, rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDouble(4), rs.getDouble(5),
                                    rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9), rs.getString(10), rs.getString(11),
                                    rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15));
                            insertQuery(newInsert);
                        }

                        rs.close();

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }
    }

    class SuperficieProcessor extends TableProcessor {

        private static final String SUPERFICIE_INSERT = "INSERT INTO PUBLIC.SUPERFICIE(DTYPE, ID, STATUS, VERSION, NOMBRE, VALORSUP, CANTIDADLOTE, UNIDADSUP_ID, CAMPO_ID, PADRE_ID) VALUES ('%s', '%s', '%s', %d, '%s', %.2f, %d, '%s', '%s', '%s')";
        private static final String ALMACENAMIENTO_INSERT = "INSERT INTO PUBLIC.SUPERFICIE_ALMACENAMIENTO(CAMPOS_ID, ALMACENAMIENTOS_ID) VALUES ('%s','%s')";
        private static final String SUPERFICIE_ALMACENAMIENTO_TABLE = "SUPERFICIE_ALMACENAMIENTO";
        List<String> almacenamientos = new ArrayList<String>();
        List<String> campos = new ArrayList<String>();
        List<String> lotes = new ArrayList<String>();
        List<String> sublotes = new ArrayList<String>();

        @Override
        void setup(String table, String[] inserts) throws Exception {
            tableName = table;
            tablesToClear.add(SUPERFICIE_ALMACENAMIENTO_TABLE);

            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", table);
            Query qry = em.createNativeQuery(selectById);

            for (String insert : inserts) {
                Matcher matcher = uuidPattern.matcher(insert);
                if (matcher.find()) {
                    String id = matcher.group();

                    qry.setParameter("id", id);
                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
                    try {
                        Statement st = tempDatabaseConnection.createStatement();
                        ResultSet rs = st.executeQuery(String.format("SELECT DTYPE, ID, STATUS, VERSION, NOMBRE, VALORSUP, CANTIDADLOTE, UNIDADSUP_ID, CAMPO_ID, DEPOSITO_ID, PADRE_ID "
                                + " FROM SUPERFICIE WHERE id = '%s'", id));


                        if (rs.next()) {
                            String campoId = rs.getString(9);
                            String padreId = rs.getString(11);
                            String type = rs.getString(1);

                            String newSuperficieInsert = String.format(SUPERFICIE_INSERT, type, rs.getString(2), rs.getString(3),
                                    rs.getInt(4), rs.getString(5), rs.getDouble(6), rs.getInt(7), rs.getString(8),
                                    campoId, padreId);
                            if (type.equals("Campo")) {
                                campos.add(newSuperficieInsert);
                            } else if (type.equals("Lote")) {
                                lotes.add(newSuperficieInsert);
                            } else if (type.equals("SubLote")) {
                                sublotes.add(newSuperficieInsert);
                            }

                            String almId = rs.getString(10);
                            if (almId != null) {
                                String almInsert = String.format(ALMACENAMIENTO_INSERT, id, almId);
                                almacenamientos.add(almInsert);
                            }
                        }

                        logger.fine(String.format("Inserting: %s", insert));
                    } catch (RuntimeException ex) {
                        throw ex;
                    }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }
                }
            }
        }

        @Override
        protected void procces() {
            queries.addAll(campos);
            queries.addAll(lotes);
            queries.addAll(sublotes);
            queries.addAll(almacenamientos);

            super.procces();
        }
    }

    class VariedadCultivoProcessor extends TableProcessor {

        public static final String VARIEDADCULTIVO_TABLE = "VARIEDADCULTIVO";
        private static final String INSERT_SQL = "INSERT INTO PUBLIC.VARIEDADCULTIVO(ID, STATUS, VERSION, DESCRIPCION, NOMBRE, CULTIVO_ID) VALUES('%s','%s',%d,'%s','%s','%s');";

        @Override
        public void setup(String table, String[] inserts) throws Exception {

            tableName = VARIEDADCULTIVO_TABLE;
//            String selectById = String.format("SELECT COUNT(*) FROM %s WHERE id = :id", VARIEDADCULTIVO_TABLE);
//            Query qry = em.createNativeQuery(selectById);


//            for (String insert : inserts) {
//                Matcher matcher = uuidPattern.matcher(insert);
//                if (matcher.find()) {
//                    String idV = matcher.group();

//                    qry.setParameter("id", idV);
//                    BigInteger count = (BigInteger) qry.getSingleResult();

//                    if (count == null || count.longValue() == 0) {
            try {
                Statement st = tempDatabaseConnection.createStatement();
                ResultSet rs = st.executeQuery(String.format("SELECT VC.ID, VC.STATUS, VC.VERSION, VC.DESCRIPCION, VC.NOMBRE, CVC.CULTIVO_ID FROM VARIEDADCULTIVO AS VC, CULTIVO_VARIEDADCULTIVO AS CVC WHERE CVC.VARIEDADES_ID = VC.ID"));

                while (rs.next()) {
                    String newInsert = String.format(INSERT_SQL, rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6));
                    insertQuery(newInsert);
                    logger.fine(String.format("Inserting: %s", newInsert));
                }

                rs.close();


            } catch (Exception ex) {
                throw ex;
            }
//                    } else {
//                        logger.fine(String.format("Discarding INSERT: %s", insert));
//                    }

//                }
//        }
        }
    }

    class ConfigurationProcessor extends TableProcessor {

        @Override
        public void setup(String table, String[] inserts) throws Exception {
            tableName = table;

            queries.addAll(Arrays.asList(inserts));
        }
    }
}

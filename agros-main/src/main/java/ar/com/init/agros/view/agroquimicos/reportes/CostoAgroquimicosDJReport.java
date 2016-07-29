package ar.com.init.agros.view.agroquimicos.reportes;

import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJChart;
import ar.com.fdvs.dj.domain.DJChartOptions;
import ar.com.fdvs.dj.domain.builders.BuilderException;
import ar.com.fdvs.dj.domain.builders.DJChartBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.init.agros.model.Agroquimico;
import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.reporting.dj.AbstractDJReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Clase CostoAgroquimicosDJReport
 *
 *
 * @author gmatheu
 * @version 10/07/2009 
 */
public abstract class CostoAgroquimicosDJReport extends AbstractDJReport
{

    private static final String AGROQUIMICO = "agroquimico";
    private static final String CAMPANIA = "campania";
    private static final String COSTO = "costo";
    private static final String CAMPO = "campo";
    private static final String CULTIVO = "cultivo";
    private static final String LOTE = "lote";
    private static final String SUBLOTE = "sublote";
    private static final String VARIEDAD = "variedad";

    protected class CostoAgroquimicosReportLine extends HashMap<String, Object>
    {

        public static final long serialVersionUID = -1L;

        public CostoAgroquimicosReportLine(String campania, String campo, String lote, String sublote, String cultivo, String variedad, String agroquimico, Double costo)
        {
            super();
            put(CAMPANIA, campania);
            put(CAMPO, campo);
            put(LOTE, lote);
            put(SUBLOTE, sublote);
            put(CULTIVO, cultivo);
            put(VARIEDAD, variedad);
            put(AGROQUIMICO, agroquimico);
            put(COSTO, costo);
        }
    }

    @Override
    protected boolean createDataSource()
    {
//        createHQLDataSource("");

        List<CostoAgroquimicosReportLine> coll = new ArrayList<CostoAgroquimicosReportLine>();
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo1", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 45.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 89.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote2", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 47.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote2", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 100.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote2", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 87.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 10.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote2", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 77.04));
        coll.add(new CostoAgroquimicosReportLine("campaña1", "campo2", "lote2", "sublote2", "cultivo2",
                "variedad1", "agroquimico1", 55.04));

        coll.add(new CostoAgroquimicosReportLine("campaña2", "campo1", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 22.04));
        coll.add(new CostoAgroquimicosReportLine("campaña2", "campo1", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 100.04));
        coll.add(new CostoAgroquimicosReportLine("campaña2", "campo1", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 100.04));
        coll.add(new CostoAgroquimicosReportLine("campaña2", "campo1", "lote1", "sublote1", "cultivo1",
                "variedad1", "agroquimico1", 100.04));

        createCollectionDataSource(coll);

        return (coll.size() > 0);
    }

    protected abstract List<Agroquimico> getAgroquimicos();

    protected abstract List<Campania> getCampanias();

    protected abstract List<Cultivo> getCultivos();

    protected abstract List<VariedadCultivo> getVariedades();

    protected abstract List<Campo> getCampos();

    protected abstract List<Lote> getLotes();

    protected abstract List<Lote> getSublotes();

    @Override
    protected void createLayout() throws BuilderException
    {
        DynamicReportBuilder drb = getReportBuilder();

        drb.setTitle("Reporte de Costos de Agroquimicos"); //defines the title of the report
        drb.setSubtitle("");
        drb.setGrandTotalLegend("Total Campaña");

        AbstractColumn campania = createGroupColumn(CAMPANIA, "Campaña:", 70);
        AbstractColumn campo = createStringColumn(CAMPO, "Campo", 85);
        AbstractColumn lote = createStringColumn(LOTE, "Lote", 85);
        AbstractColumn sublote = createStringColumn(SUBLOTE, "Sublote", 85);
        AbstractColumn cultivo = createStringColumn(CULTIVO, "Cultivo", 85);
        AbstractColumn variedad = createStringColumn(VARIEDAD, "Variedad", 85);
        AbstractColumn agroquimico = createStringColumn(AGROQUIMICO, "Agroquimico", 85);
        AbstractColumn costo = createDoubleColumn(COSTO, "Costo", 50);

        GroupBuilder gbCampania = new GroupBuilder();
        DJGroup gCampania = gbCampania.setCriteriaColumn((PropertyColumn) campania).addFooterVariable(costo,
                DJCalculation.SUM, headerVariables).setGroupLayout(
                GroupLayout.VALUE_IN_HEADER_WITH_HEADERS_AND_COLUMN_NAME).build();


        drb.addColumn(campania);
        drb.addColumn(campo);
        drb.addColumn(lote);
        drb.addColumn(sublote);
        drb.addColumn(cultivo);
        drb.addColumn(variedad);
        drb.addColumn(agroquimico);
        drb.addColumn(costo);

        drb.addGroup(gCampania);

        DJChartBuilder cb = new DJChartBuilder();
        DJChart chart =
                cb.setType(DJChart.BAR_CHART).setOperation(DJChart.CALCULATION_SUM).setColumnsGroup(gCampania).addColumn(
                costo).setPosition(DJChartOptions.POSITION_FOOTER).setShowLabels(true).build();

        drb.addChart(chart); //add chart
    }
}

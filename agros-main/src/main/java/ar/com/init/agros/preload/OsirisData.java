package ar.com.init.agros.preload;

import ar.com.init.agros.model.Accion;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.InformacionAgroquimico;
import ar.com.init.agros.model.MomentoAplicacion;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.base.BaseEntity;
import ar.com.init.agros.model.base.NamedEntity;
import ar.com.init.agros.model.costo.TipoCosto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * Clase OsirisData
 *
 *
 * @author gmatheu
 * @version 24/08/2009 
 */
public class OsirisData
{

    private List<Accion> acciones;
    private List<NamedEntity> namedEntities;
    private List<InformacionAgroquimico> informacionesAgroquimico;
    private InformacionAgroquimico selectedInformacionAgroquimico;

    public OsirisData()
    {
        acciones = new ArrayList<Accion>();
        informacionesAgroquimico = new ArrayList<InformacionAgroquimico>();
        namedEntities = new ArrayList<NamedEntity>();
    }

    public List<Accion> getAcciones()
    {
        return acciones;
    }

    public List<InformacionAgroquimico> getInformacionesAgroquimico()
    {
        return informacionesAgroquimico;
    }

    public List<BaseEntity> getEntities(Long mostSigBits)
    {
        List<BaseEntity> r = new ArrayList<BaseEntity>();
        r.addAll(acciones);
        r.addAll(informacionesAgroquimico);
        r.addAll(namedEntities);

        for (BaseEntity baseEntity : r) {
            UUID id = new UUID(mostSigBits, Long.parseLong(baseEntity.getId()));

            baseEntity.setId(id.toString());
        }

        return r;
    }

    public void addAccion(Accion a)
    {
        acciones.add(a);
    }

    public void addNamedEntity(NamedEntity ne)
    {
        namedEntities.add(ne);
    }

    public void addInformacionAgroquimico(InformacionAgroquimico toInsert)
    {
        for (InformacionAgroquimico ia : informacionesAgroquimico) {
            if (ia.equalToEntity(toInsert)) {
                return;
            }
        }

        informacionesAgroquimico.add(toInsert);
    }

    public void selectInformacionAgroquimico(String id)
    {
        for (InformacionAgroquimico informacionAgroquimico : informacionesAgroquimico) {
            if (informacionAgroquimico.getId().equals(id)) {
                selectedInformacionAgroquimico = informacionAgroquimico;
                break;
            }
        }
    }

    public void addAccionInfoAgroquimico(String id)
    {
        Accion toAdd = null;

        for (Accion accion : acciones) {
            if (accion.getId().equals(id)) {
                toAdd = accion;
                break;
            }
        }

        selectedInformacionAgroquimico.getAcciones().add(toAdd);
    }

    public static void createDigesterRules(Digester digester)
    {
        final String root = "OsirisData";
        final String acciones = "Acciones";
        final String accion = "Accion";
        final String infosAgroquimico = "InfosAgroquimico";
        final String infoAgroquimico = "InfoAgroquimico";
        final String accionesInfoAgroquimico = "AccionesInfoAgroquimico";
        final String accionInfoAgroquimico = "AccionInfoAgroquimico";
        final String cultivoPattern = root + "/Cultivos/Cultivo";
        final String tipoCostoPattern = root + "/TiposCosto/TipoCosto";

        digester.addObjectCreate(root, OsirisData.class);

        digester.addRuleSet(new NamedEntityRuleSet(Accion.class, root + "/" + acciones + "/" + accion,
                "addAccion"));

        digester.addRuleSet(new NamedEntityRuleSet(TipoCosto.class, tipoCostoPattern,
                "addNamedEntity")
        {

            @Override
            public void addRuleInstances(Digester digester)
            {
                super.addRuleInstances(digester);
                digester.addBeanPropertySetter(tipoCostoPattern + "/" + "tipo", "tipoId");
                digester.addBeanPropertySetter(tipoCostoPattern + "/" + "unidad", "unidad");
                digester.addBeanPropertySetter(tipoCostoPattern + "/" + "divisa", "divisa");

            }
        });

        digester.addRuleSet(new NamedEntityRuleSet(MomentoAplicacion.class,
                root + "/MomentosAplicacion/MomentoAplicacion", "addNamedEntity"));

        digester.addRuleSet(new NamedEntityRuleSet(Cultivo.class,
                cultivoPattern, "addNamedEntity")
        {

            @Override
            public void addRuleInstances(Digester digester)
            {
                super.addRuleInstances(digester);
                digester.addRuleSet(new NamedEntityRuleSet(VariedadCultivo.class,
                        cultivoPattern + "/VariedadesCultivo/VariedadCultivo", "addVariedad"));
            }
        });

        digester.addObjectCreate(root + "/" + infosAgroquimico + "/" + infoAgroquimico,
                InformacionAgroquimico.class);
        digester.addBeanPropertySetter(root + "/" + infosAgroquimico + "/" + infoAgroquimico + "/" + "id");
        digester.addBeanPropertySetter(
                root + "/" + infosAgroquimico + "/" + infoAgroquimico + "/" + "nombreComercial");
        digester.addBeanPropertySetter(
                root + "/" + infosAgroquimico + "/" + infoAgroquimico + "/" + "concentracion");
        digester.addBeanPropertySetter(
                root + "/" + infosAgroquimico + "/" + infoAgroquimico + "/" + "principioActivo");
        digester.addBeanPropertySetter(root + "/" + infosAgroquimico + "/" + infoAgroquimico + "/" + "pdf",
                "rutaPdf");
        digester.addSetNext(root + "/" + infosAgroquimico + "/" + infoAgroquimico,
                "addInformacionAgroquimico");

        digester.addCallMethod(
                root + "/" + accionesInfoAgroquimico + "/" + accionInfoAgroquimico + "/" + "infoAgroquimicoId",
                "selectInformacionAgroquimico", 0);
        digester.addCallMethod(
                root + "/" + accionesInfoAgroquimico + "/" + accionInfoAgroquimico + "/" + "usoId",
                "addAccionInfoAgroquimico", 0);
    }

    private static class NamedEntityRuleSet extends RuleSetBase
    {

        private Class<? extends NamedEntity> clazz;
        private String pattern;
        private String nextMethod;

        public NamedEntityRuleSet(Class<? extends NamedEntity> clazz, String pattern, String nextMethod)
        {
            this.clazz = clazz;
            this.pattern = pattern;
            this.nextMethod = nextMethod;
        }

        @Override
        public void addRuleInstances(Digester digester)
        {
            digester.addObjectCreate(pattern, clazz);
            digester.addBeanPropertySetter(pattern + "/" + "id");
            digester.addBeanPropertySetter(pattern + "/" + "nombre");
            digester.addBeanPropertySetter(pattern + "/" + "descripcion");
            digester.addSetNext(pattern, nextMethod);
        }
    }
}

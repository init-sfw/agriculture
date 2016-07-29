package ar.com.init.agros.reporting;

import ar.com.init.agros.model.Campania;
import ar.com.init.agros.model.Cultivo;
import ar.com.init.agros.model.VariedadCultivo;
import ar.com.init.agros.model.almacenamiento.Deposito;
import ar.com.init.agros.model.terreno.Campo;
import java.util.List;

/**
 * Clase ReportUtils
 *
 *
 * @author fbobbio
 * @version 15-oct-2009 
 */
public class ReportUtils
{

    public static final String armarSubtituloCampania(List<Campania> campanias)
    {
        StringBuffer ret = new StringBuffer("Campañas: ");
        for (Campania campania : campanias) {
            if (campania.equals(campanias.get(0))) {
                ret.append(campania.toString());
            }
            else {
                ret.append(", " + campania.toString());
            }
        }
        return ret.toString();
    }

    public static final String armarSubtituloCultivo(List<Cultivo> cultivos)
    {
        StringBuffer ret = new StringBuffer("Cultivos: ");
        for (Cultivo cultivo : cultivos) {
            if (cultivo.equals(cultivos.get(0))) {
                ret.append(cultivo.toString());
            }
            else {
                ret.append(", " + cultivo.toString());
            }
        }
        return ret.toString();
    }

    public static final String armarSubtituloVariedadesCultivo(List<Cultivo> cultivos, List<VariedadCultivo> variedades)
    {
        StringBuffer ret = new StringBuffer("Cultivos: ");

        for (Cultivo cultivo : cultivos) {
            StringBuffer aux = new StringBuffer(cultivo.toString());

            boolean b = false;
            aux.append(" (");
            for (VariedadCultivo vc : cultivo.getVariedades()) {
                if (variedades.contains(vc)) {

                    if (!b) {
                        aux.append(vc.toString());
                    }
                    else {
                        aux.append(", " + vc.toString());
                    }

                    b = true;
                }
            }
            aux.append(")");

            if (b) {
                if (cultivos.indexOf(cultivo) == 0) {
                    ret.append(aux.toString());
                }
                else {
                    ret.append(", " + aux.toString());
                }
            }
        }

        return ret.toString();
    }

    public static final String armarSubtituloEstablecimiento(List<Campo> campos)
    {
        StringBuffer ret = new StringBuffer("Establecimientos: ");

        for (Campo campo : campos) {
            if (campo.equals(campos.get(0))) {
                ret.append(campo.toString());
            }
            else {
                ret.append(", " + campo.toString());
            }
        }
        return ret.toString();
    }

    public static String armarSubtituloDepositos(List<Deposito> depositos)
    {
        StringBuffer ret = new StringBuffer("Depósitos: ");
        for (Deposito depo : depositos) {
            if (depo.equals(depositos.get(0))) {
                ret.append(depo.toString());
            }
            else {
                ret.append(", " + depo.toString());
            }
        }
        return ret.toString();
    }
}

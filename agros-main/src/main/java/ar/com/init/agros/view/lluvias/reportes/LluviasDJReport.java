package ar.com.init.agros.view.lluvias.reportes;

import ar.com.fdvs.dj.domain.builders.BuilderException;
import ar.com.fdvs.dj.domain.builders.DJChartBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.init.agros.model.Lluvia;
import ar.com.init.agros.model.terreno.Campo;
import ar.com.init.agros.model.terreno.Lote;
import ar.com.init.agros.reporting.dj.AbstractDJReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Clase LluviasDJReport
 *
 *
 * @author fbobbio
 * @version 21-jul-2009 
 */
public abstract class LluviasDJReport extends AbstractDJReport
{
    protected class LluviasReportLine extends HashMap<String, Object>
    {
        public static final long serialVersionUID = -1L;

        public LluviasReportLine(String fechaInicio, String fechaFin,String campo, String lote, Double lluviaCaida )
        {
            super();
            put("fechaInicio", fechaInicio);
            put("fechaFin", fechaFin);
            put("campo", campo);
            put("lote", lote);
            put("lluviaCaida", lluviaCaida);
        }
    }

    @Override
    protected boolean createDataSource()
    {
//        createHQLDataSource("");

        List<LluviasReportLine> coll = new ArrayList<LluviasReportLine>();
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo1", "lote1", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo1", "lote2", 4.4));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo2", "lote1", 5.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo2", "lote2", 87.21));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo2", "lote3", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo2", "lote4", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo3", "lote1", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo3", "lote2", 45.04));

        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo4", "lote1", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo5", "lote1", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo6", "lote1", 45.04));
        coll.add(new LluviasReportLine("10/07/2009","20/07/2009", "campo7", "lote1", 45.04));

        createCollectionDataSource(coll);

        return (coll.size() > 0);
    }

    protected abstract List<Lluvia> getLluvias();
    protected abstract List<Campo> getCampos();
    protected abstract List<Lote> getLotes();

    @Override
    protected void createLayout() throws BuilderException
    {
        DynamicReportBuilder drb = getReportBuilder();

        drb.setTitle("Reporte de Lluvias"); //defines the title of the report
        drb.setSubtitle("");
        drb.setGrandTotalLegend("Total Período");

        AbstractColumn fechaInicio = createGroupColumn("fechaInicio", "Fecha de Inicio:", 70);
        AbstractColumn fechaFin = createGroupColumn("fechaFin", "Fecha de fin:", 70);
        AbstractColumn campo = createStringColumn("campo", "Campo", 85);
        AbstractColumn lote = createStringColumn("lote", "Lote", 85);
        AbstractColumn lluviaCaida = createDoubleColumn("lluviaCaida", "Lluvia caída", 50);

        GroupBuilder gbCampania = new GroupBuilder();
        /*DJGroup gCampania = gbCampania.setCriteriaColumn((PropertyColumn) campania).addFooterVariable(lluviaCaida,
                DJCalculation.SUM, headerVariables).setGroupLayout(
                GroupLayout.VALUE_IN_HEADER_WITH_HEADERS_AND_COLUMN_NAME).build();*/


        drb.addColumn(fechaInicio);
        drb.addColumn(fechaFin);
        drb.addColumn(campo);
        drb.addColumn(lote);
        drb.addColumn(lluviaCaida);

        //drb.addGroup(gCampania);

        DJChartBuilder cb = new DJChartBuilder();
        /*DJChart chart =
                cb.setType(DJChart.BAR_CHART).setOperation(DJChart.CALCULATION_SUM).setColumnsGroup(gCampania).addColumn(
                lluviaCaida).setPosition(DJChartOptions.POSITION_FOOTER).setShowLabels(true).build();

        drb.addChart(chart); //add chart*/
    }
}

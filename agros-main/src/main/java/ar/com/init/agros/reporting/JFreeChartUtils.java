package ar.com.init.agros.reporting;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.ui.TextAnchor;

/**
 * Clase JFreeChartUtils
 *
 *
 * @author gmatheu
 * @version 29/11/2009 
 */
public class JFreeChartUtils
{

    public static List<Title> makeSubtitles(String[] strings)
    {
        List<Title> r = new ArrayList<Title>();

        for (int i = 0; i < strings.length; i++) {
            String string = strings[i].trim();

            r.add(new TextTitle(string));
        }
        return r;
    }

    public static void setUpRenderer(AbstractRenderer renderer)
    {
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("Arial", Font.PLAIN, 10));
        renderer.setItemLabelAnchorOffset(10.0);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE1, TextAnchor.BASELINE_RIGHT));

        if (renderer instanceof BarRenderer) {
            BarRenderer barRenderer = (BarRenderer) renderer;
            barRenderer.setMaximumBarWidth(0.30);
        }
    }

    public static void setSubtitles(JFreeChart chart, String[] strings)
    {
        List subtitles = makeSubtitles(strings);
        subtitles.addAll(chart.getSubtitles());
        chart.setSubtitles(subtitles);
    }
}

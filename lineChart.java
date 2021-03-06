import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class lineChart extends ApplicationFrame
{
   public lineChart( String applicationTitle , String chartTitle, DefaultCategoryDataset dataset, String xAxis, String yAxis)
   {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         xAxis,yAxis,
         createDataset(dataset),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   private CategoryDataset createDataset(DefaultCategoryDataset importedDataset )
   {
    
      final DefaultCategoryDataset dataset = importedDataset;              
      return dataset; 
   }

}
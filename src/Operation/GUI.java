package Operation;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;

public class GUI {
    public void showChartInGUI(JFreeChart chart) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close the chart window, not the whole app
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }
}

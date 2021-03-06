package org.knime.knip.leuven.nodes.window;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of WindowFeatures.
 * 
 *
 * @author Christopher Kintzel
 */
public class WindowFeaturesNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(WindowFeaturesNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_WINDOWSIZE = "Window Size";

    /** initial default count value. */
    static final int DEFAULT_CFGKEY_WINDOWSIZE = 3;

    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".
    private final SettingsModelIntegerBounded m_windowsize =
        new SettingsModelIntegerBounded(WindowFeaturesNodeModel.CFGKEY_WINDOWSIZE,
                    WindowFeaturesNodeModel.DEFAULT_CFGKEY_WINDOWSIZE,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    

    /**
     * Constructor for the node model.
     */
    protected WindowFeaturesNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	System.out.println("window size: "+m_windowsize.getIntValue());
        
        
        BufferedDataTable table = inData[0];
        DataTableSpec spec = table.getDataTableSpec();
        int columns = spec.getNumColumns();
        int featuresPerColumn = 15;
        
     // the data table spec of the single output table:
        DataColumnSpec[] allColSpecs = new DataColumnSpec[columns * featuresPerColumn];
        
        for (int i = 0; i < spec.getColumnNames().length; i++) {
			String name = spec.getColumnSpec(i).getName();
			allColSpecs[i*featuresPerColumn] = new DataColumnSpecCreator(name + " Window, Mean", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +1] = new DataColumnSpecCreator(name + " Window, Min", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +2] = new DataColumnSpecCreator(name + " Window, Max", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +3] = new DataColumnSpecCreator(name + " Window, Std", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +4] = new DataColumnSpecCreator(name + " Window, Bin 1", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +5] = new DataColumnSpecCreator(name + " Window, Bin 2", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +6] = new DataColumnSpecCreator(name + " Window, Bin 3", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +7] = new DataColumnSpecCreator(name + " Window, Bin 4", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +8] = new DataColumnSpecCreator(name + " Window, Bin 5", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +9] = new DataColumnSpecCreator(name + " Window, Change 1", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +10] = new DataColumnSpecCreator(name + " Window, Change 3", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +11] = new DataColumnSpecCreator(name + " Window, diff_neighbor_mean", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +12] = new DataColumnSpecCreator(name + " Window, diff_neighbor_min", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +13] = new DataColumnSpecCreator(name + " Window, diff_neighbor_max", DoubleCell.TYPE).createSpec();
			allColSpecs[i*featuresPerColumn +14] = new DataColumnSpecCreator(name + " Window, zscore_neighbors", DoubleCell.TYPE).createSpec();
		}
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        
        
        
        
        int counter = 0;
        ArrayList<ArrayList<Double>> valueList = new ArrayList<>();
        double[] minArr = new double[columns];
        for (int i = 0; i < minArr.length; i++) {
			minArr[i] = Double.MAX_VALUE;
		}
        double[] maxArr = new double[columns];
        for (int i = 0; i < maxArr.length; i++) {
			maxArr[i] = Double.MIN_VALUE;
		}
        
        for (DataRow dataRow : table) {
        	valueList.add(new ArrayList<Double>());
        	
        	for (int i = 0; i < dataRow.getNumCells(); i++) {
        		double value = Double.valueOf(dataRow.getCell(i).toString());
        		valueList.get(counter).add(value);
        		
        		minArr[i] = Math.min(minArr[i], value);
        		maxArr[i] = Math.max(maxArr[i], value);
			}
        	counter++;
		}
        
        
        
        
        
        

        
        
        
        
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        
        counter = 0;
        for (DataRow dataRow : table) {
        	double[][] fvList = new double[columns][];
        	
        	ArrayList<Double> list = valueList.get(counter);
        	for (int i = 0; i < list.size(); i++) {
        		ArrayList<Double> tempList = new ArrayList<Double>();
				
				int windowSize = m_windowsize.getIntValue();
				for (int j = counter - windowSize; j < counter + windowSize; j++) {
					if(j>=0 && j<valueList.size()){
						tempList.add(valueList.get(j).get(i));
					}
				}
				
				fvList[i] = getWindowFVFromList(tempList, minArr[i], maxArr[i]);
			}
        	
        	
        	
        	
        	RowKey key = dataRow.getKey();// new RowKey("Row " + i);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
        	DataCell[] cells = new DataCell[columns * featuresPerColumn];
        	
        	for (int i = 0; i < fvList.length; i++) {
				double[] fv = fvList[i];
				for (int j = 0; j < fv.length; j++) {
					cells[i*featuresPerColumn +j] = new DoubleCell(fv[j]); 
				}
			}
        	
            
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(counter / (double)table.getRowCount(), "Adding row " + counter);

        	counter++;
		}


        
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }
    
    private double[] getWindowFVFromList(ArrayList<Double> list, double binMin, double binMax){
    	
    	double mean = 0;
    	double min = Double.MAX_VALUE;
    	double max = Double.MIN_VALUE;
    	
		for (int i = 0; i < list.size(); i++) {
			double v = list.get(i);
			mean += v;
			min = Math.min(min,v);
			max = Math.max(max,v);
		}
		mean = mean/list.size();
		
		double std = 0;
		for (int i = 0; i < list.size(); i++) {
			double v = list.get(i);
			std +=(v-mean) * (v-mean);
		}
		std = Math.sqrt((1/(double)list.size()) * std);
		
		double[] bins = new double[5];
		for (int i = 0; i < list.size(); i++) {
			double v = list.get(i);
			double b = (v - binMin)/(binMax-binMin);
			if(b>=0 && b<0.2) bins[0]++;
			if(b>=0.2 && b<0.4) bins[1]++;
			if(b>=0.4 && b<0.6) bins[2]++;
			if(b>=0.6 && b<0.8) bins[3]++;
			if(b>=0.8 && b<=1.0) bins[4]++;
		}
		double bin1 = bins[0]/list.size();
		double bin2 = bins[1]/list.size();
		double bin3 = bins[2]/list.size();
		double bin4 = bins[3]/list.size();
		double bin5 = bins[4]/list.size();
		
		
		double change1 = 0; // change_window_radii = 1
		double change3 = 0; // change_window_radii = 3
		
		change1 = list.get(list.size()-1) - list.get(0);
		double change3Start = 0;
		double change3End = 0;
		if(m_windowsize.getIntValue()>=3){
			for (int i = 0; i < 3; i++) {
				change3Start += list.get(i);
				change3End += list.get(list.size() -1 -i);
			}
		}
		change3 = (change3End/3) - (change3Start/3);
		
		
		int center = (list.size()-1)/2;
		double diff_neighbor_mean = list.get(center) -mean;
		double diff_neighbor_min = list.get(center) -min;
		double diff_neighbor_max = list.get(center) -max;
		
		double zscore_neighbors = (list.get(center) - mean)/std;
		
		double[] fv = {mean, min, max, std, bin1, bin2, bin3, bin4, bin5, change1, change3, diff_neighbor_mean, diff_neighbor_min, diff_neighbor_max, zscore_neighbors};
		return fv;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
    	m_windowsize.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
    	m_windowsize.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

    	m_windowsize.validateSettings(settings);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}


package org.knime.knip.leuven.nodes.interpolation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.BooleanCell;
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
import org.knime.knip.leuven.nodes.window.MyRow;
import org.knime.knip.leuven.nodes.window.DataRowComparator;


/**
 * This is the model implementation of MovementInterpolation.
 * 
 *
 * @author Christopher Kintzel
 */
public class MovementInterpolationNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(MovementInterpolationNodeModel.class);
        

    /**
     * Constructor for the node model.
     */
    protected MovementInterpolationNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }
    
    

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {


        HashMap<String, ArrayList<DataRow>> data = new HashMap<>();
        
        int timestampMin = Integer.MAX_VALUE;
        int timestampMax = Integer.MIN_VALUE;
        
        HashSet<ArrayList> finalSet = new HashSet<ArrayList>();
        
        BufferedDataTable table = inData[0];
        for (DataRow dataRow : table) {
        	String wellPosition = dataRow.getCell(4).toString();        	
        	int timestamp = Integer.valueOf(dataRow.getCell(5).toString());
        	
        	timestampMin = Math.min(timestampMin, timestamp);
        	timestampMax = Math.max(timestampMax, timestamp);
        	
        	if(!data.containsKey(wellPosition)) data.put(wellPosition, new ArrayList<DataRow>());
        	
        	data.get(wellPosition).add(dataRow);
		}
        
        for (String key : data.keySet()){
        	System.out.println(key);
        	
        	ArrayList<DataRow> inList = data.get(key);
        	
        	Collections.sort(inList, new DataRowComparator());
        	
        	ArrayList<MyRow> outList = new ArrayList<>();
        	
        	
        	//copy first known position
        	DataRow first = inList.get(0);
        	{
        		double xHead = Double.valueOf(first.getCell(0).toString());
            	double yHead = Double.valueOf(first.getCell(1).toString());
            	double xTail = Double.valueOf(first.getCell(2).toString());
            	double yTail = Double.valueOf(first.getCell(3).toString());
            	String wellPosition = first.getCell(4).toString();
            	int timestamp = Integer.valueOf(first.getCell(5).toString());
            	boolean interpolated = false;
            	if(timestamp != timestampMin) interpolated = true;
        		outList.add(new MyRow(xHead, yHead, xTail, yTail, wellPosition, timestampMin, interpolated));
        	}
        	
        	int counter = 1;
        	for (int i = timestampMin + 1; i < timestampMax; i++) {
        		if(counter<inList.size() && Long.parseLong(inList.get(counter).getCell(5).toString()) == i){
        			
        			DataRow dataRow = inList.get(counter);
            		double xHead = Double.valueOf(dataRow.getCell(0).toString());
                	double yHead = Double.valueOf(dataRow.getCell(1).toString());
                	double xTail = Double.valueOf(dataRow.getCell(2).toString());
                	double yTail = Double.valueOf(dataRow.getCell(3).toString());
                	String wellPosition = dataRow.getCell(4).toString();
                	int timestamp = Integer.valueOf(dataRow.getCell(5).toString());
                	
                	outList.add(new MyRow(xHead, yHead, xTail, yTail, wellPosition, timestamp, false));
        			counter++;
        		} else{
    				DataRow after;
    				if(counter<inList.size()){
    					
    					//interpolation
    					after= inList.get(counter);
        				MyRow before = outList.get(outList.size()-1);
        				
        				int timestampBefore = before.timestamp;
        				int timestampAfter = Integer.parseInt(after.getCell(5).toString());
        				double timeWeight = (double)(i - timestampBefore) / (double)(timestampAfter - timestampBefore);
        	            double xHeadInterpolated = before.xHead * (1-timeWeight) + Double.valueOf(after.getCell(0).toString()) * timeWeight;
        	            double yHeadInterpolated = before.yHead * (1-timeWeight) + Double.valueOf(after.getCell(1).toString()) * timeWeight;
        	            double xTailInterpolated = before.xTail * (1-timeWeight) + Double.valueOf(after.getCell(2).toString()) * timeWeight;
        	            double yTailInterpolated = before.yTail * (1-timeWeight) + Double.valueOf(after.getCell(3).toString()) * timeWeight;
        	            outList.add(new MyRow(xHeadInterpolated, yHeadInterpolated, xTailInterpolated, yTailInterpolated, before.wellPosition, i, true));
    				} else{
    					//copy last known position
    					MyRow before = outList.get(outList.size()-1);
    					outList.add(new MyRow(before.xHead, before.yHead, before.xTail, before.yTail, before.wellPosition, i, true));
    				}
    				
        		}
				
			}
        	
        	finalSet.add(outList);
        	
        	
        }
        
        
        
        
        	DataColumnSpec[] allColSpecs = new DataColumnSpec[7];
            allColSpecs[0] = new DataColumnSpecCreator("Head[X]", DoubleCell.TYPE).createSpec();
            allColSpecs[1] = new DataColumnSpecCreator("Head[Y]", DoubleCell.TYPE).createSpec();
            allColSpecs[2] = new DataColumnSpecCreator("Tail[X]", DoubleCell.TYPE).createSpec();
            allColSpecs[3] = new DataColumnSpecCreator("Tail[Y]", DoubleCell.TYPE).createSpec();
            allColSpecs[4] = new DataColumnSpecCreator("Well Position", StringCell.TYPE).createSpec();
            allColSpecs[5] = new DataColumnSpecCreator("timestamp", IntCell.TYPE).createSpec();
            allColSpecs[6] = new DataColumnSpecCreator("interpolated", BooleanCell.TYPE).createSpec();

            DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
            BufferedDataContainer container = exec.createDataContainer(outputSpec);
            
            
            int counter = 0;
            for (ArrayList<MyRow> list : finalSet){
            	for (int j = 0; j < list.size(); j++) {
            		MyRow myRow = list.get(j);
            		
            		RowKey key = new RowKey("Row " + counter);
                	DataCell[] cells = new DataCell[7];
                	cells[0] = new DoubleCell(myRow.xHead);
                	cells[1] = new DoubleCell(myRow.yHead);
                	cells[2] = new DoubleCell(myRow.xTail);
                	cells[3] = new DoubleCell(myRow.yTail);
                	cells[4] = new StringCell(myRow.wellPosition);
                	cells[5] = new IntCell(myRow.timestamp);
                	cells[6] = BooleanCell.get(myRow.interpolated);
                	
                    DataRow row = new DefaultRow(key, cells);
                    container.addRowToTable(row);

                	counter++;
				}
            }
            
        
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
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


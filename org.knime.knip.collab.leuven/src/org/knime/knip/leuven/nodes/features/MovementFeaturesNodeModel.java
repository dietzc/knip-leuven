package org.knime.knip.leuven.nodes.features;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of MovementFeatures.
 * Movement Features
 *
 * @author Christopher Kintzel
 */
public class MovementFeaturesNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(MovementFeaturesNodeModel.class);
            

    /**
     * Constructor for the node model.
     */
    protected MovementFeaturesNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }
    
    private static double euclideanDistance(double x1, double y1, double x2, double y2) {
        double a = x1 - x2;
        double b = y1 - y2;
        return Math.sqrt(a * a + b * b);
    }
    
    private static double angleXY(double x1, double y1, double x2, double y2, double x3, double y3){
        double a = euclideanDistance(x2, y2, x3, y3);
        double b = euclideanDistance(x1, y1, x3, y3);
        double c = euclideanDistance(x1, y1, x2, y2);
        double angle = Math.acos((a*a+ c*c - b*b)/(2*a*c));
        //System.out.println(a+" "+b+" "+c+" "+angle+" \t"+x1+","+y1+" \t"+x2+","+y2+" \t"+x3+","+y3);
        return angle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
        
        BufferedDataTable table = inData[0];
        int numColumns = table.getDataTableSpec().getNumColumns();
        
        
        DataColumnSpec[] allColSpecs = new DataColumnSpec[83 + numColumns];
        
        for (int i = 0; i < numColumns; i++) {
        	allColSpecs[i] = table.getDataTableSpec().getColumnSpec(i);
		}
        int columnCounter = numColumns;
        
        String[] points = {"P1", "P2", "C"};
        for (String string : points) { // 3*24
        	allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" X", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Y", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed X", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Y", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Change", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Change X", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Change Y", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Change Absolute", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Change Absolute X", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Speed Change Absolute Y", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Angle", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Angle Change", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Angle Change Absolute", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Radial Coordinate", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Angular Coordinate", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Radial Velocity", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Tangential Velocity", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Radial Velocity Absolute", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Tangential Velocity Absolute", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Radial Velocity Change", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Tangential Velocity Change", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Radial Velocity Change Absolute", DoubleCell.TYPE).createSpec();
            allColSpecs[columnCounter++] = new DataColumnSpecCreator(string+" Tangential Velocity Change Absolute", DoubleCell.TYPE).createSpec();
		}
        
        
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("P1-P2-Distance", DoubleCell.TYPE).createSpec();
        
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Radial Coordinate", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Angular Coordinate", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Radial Velocity / P1-P2-Distance Change", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Tangential Velocity / Internal Rotation", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Radial Velocity Absolute / P1-P2-Distance Change Absolute", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Tangential Velocity Absolute / Internal Rotation Absolute", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Radial Velocity Change / P1-P2-Distance Acceleration", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Tangential Velocity Change / Internal Rotation Change", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Radial Velocity Change Absolute/ P1-P2-Distance Acceleration Absolute", DoubleCell.TYPE).createSpec();
        allColSpecs[columnCounter++] = new DataColumnSpecCreator("Movement Direction Tangential Velocity Change Absolute / Internal Rotation Change Absolute", DoubleCell.TYPE).createSpec();
        
        
        
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        
        
        
        LinkedList<Double[]> prevQueue = new LinkedList<>(); // 0 = prev3, 1 = prev2, 2 = prev
        int counter = 0;
        String lastId = "";
        for (DataRow dataRow : table) {
        	double xHead = Double.valueOf(dataRow.getCell(0).toString());
        	double yHead = Double.valueOf(dataRow.getCell(1).toString());
        	double xTail = Double.valueOf(dataRow.getCell(2).toString());
        	double yTail = Double.valueOf(dataRow.getCell(3).toString());
        	
        	String id = dataRow.getCell(4).toString(); // must be sorted by column #5 (id, wellPosition, ...)
        	if(!id.equals(lastId)){
        		lastId = id;
                prevQueue.clear();
                Double[] prev = {0.0,0.0,0.0,0.0,0.0,0.0};
            	prevQueue.offer(prev);
            	prevQueue.offer(prev);
            	prevQueue.offer(prev);
        	}
        	
        	
        	double xCenter = (xHead+xTail)/2;
        	double yCenter = (yHead+yTail)/2;
        	
        	double[] fv1 = getFVFromPoint(xHead, 	yHead, 	 prevQueue.get(2)[0], prevQueue.get(2)[1], prevQueue.get(1)[0], prevQueue.get(1)[1], prevQueue.get(0)[0], prevQueue.get(0)[1]);
        	double[] fv2 = getFVFromPoint(xTail, 	yTail, 	 prevQueue.get(2)[2], prevQueue.get(2)[3], prevQueue.get(1)[2], prevQueue.get(1)[3], prevQueue.get(0)[2], prevQueue.get(0)[3]);
        	double[] fv3 = getFVFromPoint(xCenter, yCenter,  prevQueue.get(2)[4], prevQueue.get(2)[5], prevQueue.get(1)[4], prevQueue.get(1)[5], prevQueue.get(0)[4], prevQueue.get(0)[5]);
        	double htDistance = euclideanDistance(xHead, yHead, xTail, yTail);
        	
        	RowKey key = dataRow.getKey();// new RowKey("Row " + i);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
        	DataCell[] cells = new DataCell[83 + numColumns];
        	
        	for (int i = 0; i < numColumns; i++) {
        		cells[i] = dataRow.getCell(i);
    		}
        	
        	columnCounter = numColumns;
        	for (int i = 0; i < fv1.length; i++) {
				cells[columnCounter++] = new DoubleCell(fv1[i]);
			}
        	for (int i = 0; i < fv2.length; i++) {
				cells[columnCounter++] = new DoubleCell(fv2[i]);
			}
        	for (int i = 0; i < fv3.length; i++) {
				cells[columnCounter++] = new DoubleCell(fv3[i]);
			}
            
            cells[columnCounter++] = new DoubleCell(htDistance); 
            
            // internal rotation
            double timediff = 1.0/1.0;
            	double x = xHead - xCenter;
            	double y = yHead - yCenter;
        		double prevX = prevQueue.get(2)[0] - prevQueue.get(2)[4];
        		double prevY = prevQueue.get(2)[1] - prevQueue.get(2)[5];
    			double prevX2 = prevQueue.get(1)[0] - prevQueue.get(1)[4];
        		double prevY2 = prevQueue.get(1)[1] - prevQueue.get(1)[5];
        	double radialCoordinate = Math.sqrt(x*x + y*y);
        	double angularCoordinate = Math.atan2(x, y);
        		double prevRadialCoordinate = Math.sqrt(prevX*prevX + prevY*prevY);
        		double prevAngularCoordinate = Math.atan2(prevX, prevY);
        		double prevRadialCoordinate2 = Math.sqrt(prevX2*prevX2 + prevY2*prevY2);
        		double prevAngularCoordinate2 = Math.atan2(prevX2, prevY2);
        	double radialVelocity = (radialCoordinate - prevRadialCoordinate)/timediff;
            double tangentialVelocity = (angularCoordinate - prevAngularCoordinate)/timediff;
            double absoluteRadialVelocity = Math.abs(radialVelocity);
            double absoluteTangentialVelocity = Math.abs(tangentialVelocity);
            	
            	double prevRadialVelocity = (prevRadialCoordinate - prevRadialCoordinate2)/timediff;
            	double prevTangentialVelocity = (prevAngularCoordinate - prevAngularCoordinate2)/timediff;
            
            double radialVelocityChange = radialVelocity - prevRadialVelocity;
            double absoluteRadialVeolictyChange = Math.abs(radialVelocityChange);
            double tangentialVelocityChange = tangentialVelocity - prevTangentialVelocity;
            double absoluteTangentialVelocityChange = Math.abs(tangentialVelocityChange);
            
            
            cells[columnCounter++] = new DoubleCell(radialCoordinate);
            cells[columnCounter++] = new DoubleCell(angularCoordinate);
            cells[columnCounter++] = new DoubleCell(radialVelocity);
            cells[columnCounter++] = new DoubleCell(tangentialVelocity);
            cells[columnCounter++] = new DoubleCell(absoluteRadialVelocity);
            cells[columnCounter++] = new DoubleCell(absoluteTangentialVelocity);
            cells[columnCounter++] = new DoubleCell(radialVelocityChange);
            cells[columnCounter++] = new DoubleCell(absoluteRadialVeolictyChange);
            cells[columnCounter++] = new DoubleCell(tangentialVelocityChange);
            cells[columnCounter++] = new DoubleCell(absoluteTangentialVelocityChange);

            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(counter / (double)table.getRowCount(), "Adding row " + counter);
        	
            Double[] prev = new Double[6];
        	prev[0] = xHead;
        	prev[1] = yHead;
        	prev[2] = xTail;
        	prev[3] = yTail;
        	prev[4] = xCenter;
        	prev[5] = yCenter;
        	prevQueue.offer(prev);
        	if(prevQueue.size() > 3) prevQueue.poll();
        	
        	counter++;
		}


        
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }
    
    
    private double[] getFVFromPoint(double x, double y, double prevX, double prevY, double prevX2, double prevY2, double prevX3, double prevY3){
    	double timediff = 1.0/1.0;
    	double speed = euclideanDistance(x, y, prevX, prevY)/timediff;
    	double speedX = euclideanDistance(x, 0, prevX, 0)/timediff;
    	double speedY = euclideanDistance(0, y, 0, prevY)/timediff;
    	
    	double prevSpeed = euclideanDistance(prevX, prevY, prevX2, prevY2)/timediff;
    	double prevSpeedX = euclideanDistance(prevX, 0, prevX2, 0)/timediff;
    	double prevSpeedY = euclideanDistance(0, prevY, 0, prevY2)/timediff;
    	
    	double speedChange = speed - prevSpeed;
    	double speedChangeX = speedX - prevSpeedX;
    	double speedChangeY = speedY - prevSpeedY;
    	
    	double speedChangeAbsolute = Math.abs(speedChange);
    	double speedChangeAbsoluteX = Math.abs(speedChangeX);
    	double speedChangeAbsoluteY = Math.abs(speedChangeY);
    	
    	
    	double angle = angleXY(x, y, prevX, prevY, prevX2, prevY2);
    	double prevAngle = angleXY(prevX, prevY, prevX2, prevY2, prevX3, prevY3);
    	
    	double angleChange = angle - prevAngle;
    	double angleChangeAbsolute = Math.abs(angleChange);
    	
    	
    	//polar coordinates
    	double radialCoordinate = Math.sqrt(x*x + y*y);
    	double angularCoordinate = Math.atan2(x, y);
    		double prevRadialCoordinate = Math.sqrt(prevX*prevX + prevY*prevY);
    		double prevAngularCoordinate = Math.atan2(prevX, prevY);
    		double prevRadialCoordinate2 = Math.sqrt(prevX2*prevX2 + prevY2*prevY2);
    		double prevAngularCoordinate2 = Math.atan2(prevX2, prevY2);
    	
    	double radialVelocity = (radialCoordinate - prevRadialCoordinate)/timediff;
    	double tangentialVelocity = (angularCoordinate - prevAngularCoordinate)/timediff;
    	double absoluteRadialVelocity = Math.abs(radialVelocity);
    	double absoluteTangentialVelocity = Math.abs(tangentialVelocity);
    	
    		double prevRadialVelocity = (prevRadialCoordinate - prevRadialCoordinate2)/timediff;
    		double prevTangentialVelocity = (prevAngularCoordinate - prevAngularCoordinate2)/timediff;
    	
    	double radialVelocityChange = radialVelocity - prevRadialVelocity;
    	double absoluteRadialVeolictyChange = Math.abs(radialVelocityChange);
    	double tangentialVelocityChange = tangentialVelocity - prevTangentialVelocity;
    	double absoluteTangentialVelocityChange = Math.abs(tangentialVelocityChange);
    	//System.out.println(x+" "+y+" "+pcR+" "+pcA);
    	
    	
    	
    	double[] fv = {
    			x,y,
    			speed,speedX,speedY,
    			speedChange,speedChangeX,speedChangeY,
    			speedChangeAbsolute,speedChangeAbsoluteX,speedChangeAbsoluteY,
    			angle,angleChange,angleChangeAbsolute,
    			radialCoordinate, angularCoordinate,
    			radialVelocity, tangentialVelocity, absoluteRadialVelocity, absoluteTangentialVelocity,
    			radialVelocityChange, tangentialVelocityChange, absoluteRadialVeolictyChange, absoluteTangentialVelocityChange
    			};
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


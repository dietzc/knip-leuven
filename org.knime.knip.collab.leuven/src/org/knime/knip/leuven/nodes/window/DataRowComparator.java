package org.knime.knip.leuven.nodes.window;

import java.util.Comparator;

import org.knime.core.data.DataRow;

public class DataRowComparator implements Comparator<DataRow>{

	@Override
	public int compare(DataRow o1, DataRow o2) {
		long time1 = Long.parseLong(o1.getCell(5).toString()); //timestamp
		long time2 = Long.parseLong(o2.getCell(5).toString());
		
		return Long.compare(time1, time2);
	}
	
}

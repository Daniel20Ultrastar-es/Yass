package yass;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 *  Descriptinoutn of the Class
 *
 * @author     Saruta
 * @created    4. September 2006
 */
public class YassTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 879831241165423284L;
	private static String[] columnNames = {"", I18.get("table_col_1"), I18.get("table_col_2"), I18.get("table_col_3"), I18.get("table_col_4")};
	private Vector<YassRow> data = new Vector<YassRow>(3000, 1000);


	/**
	 *  Constructor for the YassTableModel object
	 */
	public YassTableModel() {
		super();
	}


	/**
	 *  Gets the data attribute of the YassTableModel object
	 *
	 * @return    The data value
	 */
	public Vector<YassRow> getData() {
		return data;
	}


	/**
	 *  Sets the data attribute of the YassTableModel object
	 *
	 * @param  d  The new data value
	 */
	public void setData(Vector<YassRow> d) {
		data = d;
	}


	/**
	 *  Gets the columnCount attribute of the MyTableModel object
	 *
	 * @return    The columnCount value
	 */
	public int getColumnCount() {
		return columnNames.length;
	}


	/**
	 *  Gets the rowCount attribute of the MyTableModel object
	 *
	 * @return    The rowCount value
	 */
	public int getRowCount() {
		return data.size();
	}


	/**
	 *  Gets the columnName attribute of the MyTableModel object
	 *
	 * @param  col  Descriptinoutn of the Parameter
	 * @return      The columnName value
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}


	/**
	 *  Gets the valueAt attribute of the MyTableModel object
	 *
	 * @param  row  Descriptinoutn of the Parameter
	 * @param  col  Descriptinoutn of the Parameter
	 * @return      The valueAt value
	 */
	public Object getValueAt(int row, int col) {
		if (row > data.size()) {
			return "";
		}
		// concurrency problem at startup
		YassRow r = (YassRow) data.elementAt(row);
		if (r == null) {
			return "";
		}
		// concurrency problem at startup
		return r.elementAt(col);
	}


	/**
	 *  Gets the columnClass attribute of the MyTableModel object
	 *
	 * @param  c  Descriptinoutn of the Parameter
	 * @return    The columnClass value
	 */
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}


	/**
	 *  Gets the cellEditable attribute of the MyTableModel object
	 *
	 * @param  row  Descriptinoutn of the Parameter
	 * @param  col  Descriptinoutn of the Parameter
	 * @return      The cellEditable value
	 */
	public boolean isCellEditable(int row, int col) {
		YassRow r = (YassRow) data.elementAt(row);
		if (col == 0) {
			return false;
		}
		if (r.isComment()) {
			return false;
		}
		if (r.isPageBreak() && col > 2) {
			return false;
		}
		if (r.isNote() && col > 3) {
			return false;
		}
		return true;
	}


	/**
	 *  Sets the valueAt attribute of the MyTableModel object
	 *
	 * @param  value  The new valueAt value
	 * @param  row    The new valueAt value
	 * @param  col    The new valueAt value
	 */
	public void setValueAt(Object value, int row, int col) {
		YassRow r = (YassRow) data.elementAt(row);
		if (r.isNoteOrPageBreak()) {
			if (col >= 1 && col <= 3) {
				try {
					value = (String) (Integer.parseInt((String) value) + "");
				}
				catch (Exception e) {
					return;
				}
			}
		}
		r.setElementAt((String) value, col);
		fireTableCellUpdated(row, col);
	}


	/**
	 *  Adds a feature to the Row attribute of the MyTableModel object
	 *
	 * @param  type    The feature to be added to the Row attribute
	 * @param  time    The feature to be added to the Row attribute
	 * @param  length  The feature to be added to the Row attribute
	 * @param  height  The feature to be added to the Row attribute
	 * @param  txt     The feature to be added to the Row attribute
	 * @param  err     The feature to be added to the Row attribute
	 * @param  detail  The feature to be added to the Row attribute
	 */
	public void addRow(String type, String time, String length, String height, String txt, String err, String detail) {
		data.addElement(new YassRow(type, time, length, height, txt, err, detail));
	}


	/**
	 *  Adds a feature to the Row attribute of the YassTableModel object
	 *
	 * @param  type    The feature to be added to the Row attribute
	 * @param  time    The feature to be added to the Row attribute
	 * @param  length  The feature to be added to the Row attribute
	 * @param  height  The feature to be added to the Row attribute
	 * @param  txt     The feature to be added to the Row attribute
	 * @param  err     The feature to be added to the Row attribute
	 */
	public void addRow(String type, String time, String length, String height, String txt, String err) {
		data.addElement(new YassRow(type, time, length, height, txt, err));
	}


	/**
	 *  Adds a feature to the Row attribute of the YassTableModel object
	 *
	 * @param  type    The feature to be added to the Row attribute
	 * @param  time    The feature to be added to the Row attribute
	 * @param  length  The feature to be added to the Row attribute
	 * @param  height  The feature to be added to the Row attribute
	 * @param  txt     The feature to be added to the Row attribute
	 */
	public void addRow(String type, String time, String length, String height, String txt) {
		data.addElement(new YassRow(type, time, length, height, txt));
	}


	/**
	 *  Adds a feature to the Row attribute of the YassTableModel object
	 *
	 * @param  r  The feature to be added to the Row attribute
	 */
	public void addRow(YassRow r) {
		data.addElement(new YassRow(r));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  type    Description of the Parameter
	 * @param  time    Description of the Parameter
	 * @param  length  Description of the Parameter
	 * @param  height  Description of the Parameter
	 * @param  txt     Description of the Parameter
	 * @param  i       Description of the Parameter
	 */
	public void insertRowAt(String type, String time, String length, String height, String txt, int i) {
		data.insertElementAt(new YassRow(type, time, length, height, txt), i);
	}


	/**
	 *  Sets the rowAt attribute of the YassTableModel object
	 *
	 * @param  type    The new rowAt value
	 * @param  time    The new rowAt value
	 * @param  length  The new rowAt value
	 * @param  height  The new rowAt value
	 * @param  txt     The new rowAt value
	 * @param  i       The new rowAt value
	 */
	public void setRowAt(String type, String time, String length, String height, String txt, int i) {
		YassRow r = (YassRow) data.elementAt(i);
		r.setRow(type, time, length, height, txt);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  i  Description of the Parameter
	 */
	public void removeRowAt(int i) {
		data.removeElementAt(i);
	}


	/**
	 *  Gets the rowAt attribute of the YassTableModel object
	 *
	 * @param  row  Description of the Parameter
	 * @return      The rowAt value
	 */
	public YassRow getRowAt(int row) {
		if (row < 0 || row >= data.size()) {
			return null;
		}
		return (YassRow) data.elementAt(row);
	}


	/**
	 *  Gets the commentRow attribute of the YassTableModel object
	 *
	 * @param  ctype  Description of the Parameter
	 * @return        The commentRow value
	 */
	public YassRow getCommentRow(String ctype) {
		int n = data.size();
		int i = 0;
		while (i < n) {
			YassRow r = (YassRow) data.elementAt(i++);
			if (r.isComment() && r.getCommentTag().equals(ctype)) {
				return r;
			}
		}
		return null;
	}


	/**
	 *  Gets the endRow attribute of the YassTableModel object
	 *
	 * @return    The endRow value
	 */
	public YassRow getEndRow() {
		int n = data.size();
		int i = n - 1;
		while (i > 0) {
			YassRow r = (YassRow) data.elementAt(i--);
			if (r.isEnd()) {
				return r;
			}
		}
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public Hashtable<String, Vector<YassRow>> collectMessages() {
		Hashtable<String, Vector<YassRow>> t = new Hashtable<String, Vector<YassRow>>();
		for (Enumeration<YassRow> e = data.elements(); e.hasMoreElements(); ) {
			YassRow r = (YassRow) e.nextElement();
			if (!r.hasMessage()) {
				continue;
			}
			Vector<?> messages = r.getMessages();
			for (Enumeration<?> e2 = messages.elements(); e2.hasMoreElements(); ) {
				String[] msg = (String[]) e2.nextElement();

				Vector<YassRow> list = (Vector<YassRow>) t.get(msg[0]);
				if (list == null) {
					t.put(msg[0], list = new Vector<YassRow>());
				}
				list.addElement(r);
			}
		}
		return t;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  tm  Description of the Parameter
	 * @return     Description of the Return Value
	 */
	public boolean equalsData(YassTableModel tm) {
		if (data.size() != tm.data.size()) {
			return false;
		}
		int n = data.size();
		for (int i = 0; i < n; i++) {
			if (!getRowAt(i).equals(tm.getRowAt(i))) {
				return false;
			}
		}
		return true;
	}
}

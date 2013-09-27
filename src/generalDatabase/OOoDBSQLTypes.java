package generalDatabase;

import java.sql.Types;

public class OOoDBSQLTypes extends SQLTypes {

	@Override
	public String typeToString(int sqlType, int length, boolean counter) {
		if (sqlType == Types.INTEGER && counter) {
			return "INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1)";
		}
		return super.typeToString(sqlType, length, counter);
	}

	/* (non-Javadoc)
	 * @see generalDatabase.SQLTypes#formatColumnName(java.lang.String)
	 */
	@Override
	public synchronized String formatColumnName(String columnName) {
		// database system only supports upper case column names !
		if (columnName == null) {
			return null;
		}
		return "\"" +columnName.toUpperCase()+ "\"";
		
	}
	

}

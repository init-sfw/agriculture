package ar.com.init.agros.model.base.type;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

/**
 * @author gmatheu
 */
public class EnumUserType implements EnhancedUserType, ParameterizedType {

    protected Class<Enum> enumClass;
    protected Method idMethod;
    private static final String DEFAULT_ID_METHOD = "id";

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            enumClass = (Class<Enum>) Class.forName(enumClassName);
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }

        findMethodName(parameters);
    }

    protected void findMethodName(Properties parameters) throws HibernateException {
        String idMethodName = parameters.getProperty("idMethodName");
        if (idMethodName == null) {
            idMethodName = DEFAULT_ID_METHOD;
        }
        try {
            idMethod = enumClass.getDeclaredMethod(idMethodName);
        } catch (NoSuchMethodException ex) {
            throw new HibernateException("Method not found", ex);
        } catch (SecurityException ex) {
            throw new HibernateException("SecurityException", ex);
        }
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum) value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        String name = rs.getString(names[0]);
        return rs.wasNull() ? null : getEnumValue(name);
    }

    private Object getEnumValue(String id) {
        Enum value = null;
        try {
            Enum[] values = enumClass.getEnumConstants();

            for (Enum val : values) {

                if (idMethod.invoke(val).equals(id)) {
                    value = val;
                    break;
                }
            }

        } catch (IllegalAccessException ex) {
            throw new HibernateException("IllegalAccessException", ex);
        } catch (IllegalArgumentException ex) {
            throw new HibernateException("IllegalArgumentException", ex);
        } catch (InvocationTargetException ex) {
            throw new HibernateException("InvocationTargetException", ex);
        } catch (SecurityException ex) {
            throw new HibernateException("SecurityException", ex);
        }

        return value;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, getEnumId(value));
        }
    }

    private String getEnumId(Object value) {
        String r = null;

        try {
            Enum[] values = enumClass.getEnumConstants();

            for (Enum val : values) {

                if (val.equals(value)) {
                    r = idMethod.invoke(val).toString();
                    break;
                }
            }

        } catch (IllegalAccessException ex) {
            throw new HibernateException("IllegalAccessException", ex);
        } catch (IllegalArgumentException ex) {
            throw new HibernateException("IllegalArgumentException", ex);
        } catch (InvocationTargetException ex) {
            throw new HibernateException("InvocationTargetException", ex);
        } catch (SecurityException ex) {
            throw new HibernateException("SecurityException", ex);
        }

        return r;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    public Class returnedClass() {
        return enumClass;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(enumClass, xmlValue);
    }

    @Override
    public String objectToSQLString(Object value) {
        return '\'' + ((Enum) value).name() + '\'';
    }

    @Override
    public String toXMLString(Object value) {
        return ((Enum) value).name();
    }
}

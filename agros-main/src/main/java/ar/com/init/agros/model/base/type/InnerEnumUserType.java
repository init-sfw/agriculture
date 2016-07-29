package ar.com.init.agros.model.base.type;

import java.util.Properties;
import org.hibernate.HibernateException;

/**
 *
 * @author gmatheu
 */
public class InnerEnumUserType extends EnumUserType {

    @Override
    public void setParameterValues(Properties parameters) {
        String className = parameters.getProperty("className");

        try {
            Class clazz = (Class) Class.forName(className);

            String enumClassName = parameters.getProperty("enumClassName");

            for (Class c : clazz.getClasses()) {

                if (c.getSimpleName().equals(enumClassName)) {
                    enumClass = c;
                    break;
                }
            }
            findMethodName(parameters);

        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }
}

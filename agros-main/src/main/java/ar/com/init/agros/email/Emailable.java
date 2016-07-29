package ar.com.init.agros.email;

import ar.com.init.agros.model.util.EMailMessage;

/**
 * Interfaz Emailable
 *
 *
 * @author gmatheu
 * @version 13/07/2009 
 */

public interface Emailable 
{
    /**
     * Crea un mensaje con el contenido apropiado.
     * No hace falta configurar los remitentes , ni destinatarios; solo el contenido del mensaje y el asunto
     * @return mensaje para mandar
     */
    public EMailMessage createEmailMessage();
}

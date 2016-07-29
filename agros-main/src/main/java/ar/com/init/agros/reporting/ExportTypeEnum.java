/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.com.init.agros.reporting;

/**
 * Tipode de formatos disponibles para exportar reportes DynamicJasper
 *
 * @author gmatheu
 */
public enum ExportTypeEnum {

    XLS(".xls", "Excel"),
    PLAIN_XLS(".xls", "Excel Plano"),
    PDF(".pdf", "pdf");

    private final String descripcion;
    private final String extension;

    ExportTypeEnum(String extension, String desc) {
        this.descripcion = desc;
        this.extension = extension;
    }

    public String descripcion() {
        return descripcion;
    }

    public String extension() {
        return extension;
    }
}

package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"idProductoItem"})
public class RenglonFactura implements Serializable {

    private long idRenglonFactura;
    private long idProductoItem;
    private String codigoItem;
    private String descripcionItem;
    private String medidaItem;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal bonificacionPorcentaje;
    private BigDecimal bonificacionNeta;
    private BigDecimal ivaPorcentaje;
    private BigDecimal ivaNeto;
    private BigDecimal gananciaPorcentaje;
    private BigDecimal gananciaNeto;
    private BigDecimal importe;
}

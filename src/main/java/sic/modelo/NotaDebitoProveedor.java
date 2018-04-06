package sic.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotaDebitoProveedor extends NotaDebito implements Serializable {
    
    private Proveedor proveedor;

    public NotaDebitoProveedor() {
    }

    public NotaDebitoProveedor(long idNota, long serie, long nroNota, boolean eliminada,
            TipoDeComprobante tipoDeComprobante, Date fecha, Empresa empresa, Usuario usuario, String motivo, List<RenglonNotaDebito> renglones,
            BigDecimal subTotalBruto, BigDecimal iva21Neto, BigDecimal iva105Neto, BigDecimal total, BigDecimal montoNoGravado, long CAE,
            Date vencimientoCAE, long numSerieAfip, long numNotaAfip, Recibo recibo, Proveedor proveedor) {

        super(idNota, serie, nroNota, eliminada, tipoDeComprobante, fecha, empresa, usuario, motivo, renglones,
                subTotalBruto, iva21Neto, iva105Neto, total, montoNoGravado, CAE, vencimientoCAE, numSerieAfip, numNotaAfip, recibo);
        this.proveedor = proveedor;
    }
}

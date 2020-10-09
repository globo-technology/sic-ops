package sic.modelo.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sic.modelo.TipoDeComprobante;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaFacturaCompraCriteria {

    private LocalDateTime fechaAltaDesde;
    private LocalDateTime fechaAltaHasta;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private Long idProveedor;
    private Long numSerie;
    private Long numFactura;
    private TipoDeComprobante tipoComprobante;
    private Long idProducto;
    private Long idSucursal;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;

}

package sic.modelo.criteria;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sic.modelo.TipoDeComprobante;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaFacturaCompraCriteria {

    private Date fechaDesde;
    private Date fechaHasta;
    private Long idProveedor;
    private Long numSerie;
    private Long numFactura;
    private TipoDeComprobante tipoComprobante;
    private Long idProducto;
    private Long idEmpresa;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;

}

package sic.modelo.criteria;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sic.modelo.TipoDeComprobante;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaFacturaVentaCriteria {

    private Date fechaDesde;
    private Date fechaHasta;
    private Long idCliente;
    private TipoDeComprobante tipoComprobante;
    private Long idUsuario;
    private Long idViajante;
    private Long numSerie;
    private Long numFactura;
    private Long nroPedido;
    private Long idProducto;
    private Long idEmpresa;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}

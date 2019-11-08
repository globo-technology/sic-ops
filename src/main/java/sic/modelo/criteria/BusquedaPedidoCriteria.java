package sic.modelo.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sic.modelo.EstadoPedido;
import sic.modelo.TipoDeEnvio;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaPedidoCriteria {

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private Long idCliente;
    private Long idUsuario;
    private Long idViajante;
    private Long nroPedido;
    private EstadoPedido estadoPedido;
    private TipoDeEnvio tipoDeEnvio;
    private Long idProducto;
    private Long idSucursal;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}
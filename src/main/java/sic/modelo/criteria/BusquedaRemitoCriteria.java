package sic.modelo.criteria;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sic.modelo.TipoDeComprobante;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaRemitoCriteria {

  private LocalDateTime fechaDesde;
  private LocalDateTime fechaHasta;
  private Long serie;
  private Long nroRemito;
  private TipoDeComprobante tipoDeRemito;
  private Long idCliente;
  private Long idSucursal;
  private Long idUsuario;
  private Integer pagina;
  private String ordenarPor;
  private String sentido;
}

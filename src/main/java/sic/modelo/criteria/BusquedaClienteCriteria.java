package sic.modelo.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusquedaClienteCriteria {

    private boolean buscaPorNombreFiscal;
    private String nombreFiscal;
    private boolean buscaPorNombreFantasia;
    private String nombreFantasia;
    private boolean buscaPorIdFiscal;
    private Long idFiscal;
    private boolean buscaPorViajante;
    private Long idViajante;
    private boolean buscaPorProvincia;
    private Long idProvincia;
    private boolean buscaPorLocalidad;
    private Long idLocalidad;
    private boolean buscarPorNroDeCliente;
    private String nroDeCliente;
    private Long idEmpresa;
    private Integer pagina;
    private String ordenarPor;
    private String sentido;
}

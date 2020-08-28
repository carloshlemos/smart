package gov.goias.smartapi.representation;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Data
public class UsuarioRep {

    private String nomeUsuario;
    private Integer codigoUnidade;
    private String nomeUnidade;
    private String numeroCpf;

}



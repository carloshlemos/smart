package gov.goias.smartapi.representation;

import gov.goias.AlmPublisher;
import gov.goias.GitRepositoryState;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;


@XmlRootElement
@Data
public class ToolbarRep {

    public ToolbarRep() throws IOException {

    }

    public ToolbarRep(UsuarioRep usuario, AlmPublisher.AlmRep alm) throws IOException {
        this.usuario = usuario;
        this.alm = alm;
    }

    private UsuarioRep usuario;
    private AlmPublisher.AlmRep alm;
}

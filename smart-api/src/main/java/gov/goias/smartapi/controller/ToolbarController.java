package gov.goias.smartapi.controller;

import gov.goias.AlmPublisher;
import gov.goias.cas.auth.CasUserExtractor;
import gov.goias.exceptions.AcessoNegadoException;
import gov.goias.smartapi.representation.Sessao;
import gov.goias.smartapi.representation.ToolbarRep;
import gov.goias.smartapi.representation.UsuarioRep;
import javaslang.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class ToolbarController {
    @Autowired
    private CasUserExtractor userExtractor;

    @Autowired
    private HttpServletRequest req;

    @Inject
    private Sessao sessao;

    @GetMapping("/toolbar")
    public ToolbarRep toolbar() throws IOException {
        final String username = userExtractor.extractUser(req)
                .orElseThrow(() -> AcessoNegadoException.at(req.getRequestURI()));

        final UsuarioRep u = new UsuarioRep();

        String cpf = userExtractor.cpf(req).get();
        sessao.setUsuarioCpf(cpf);
        sessao.setUsuario(userExtractor.nome(req).get());
        sessao.setNomeUsuario(username);
        sessao.setQtdeLogin(sessao.getQtdeLogin() + 1);

        //sessao.setUsuarioCpf("31081371153");
        u.setNomeUsuario(username);
        u.setNumeroCpf(cpf);

        final AlmPublisher.AlmRep almRep = Try.of(() -> AlmPublisher.publish(req))
                .map(map -> AlmPublisher.toRep(map))
                .recover(e -> new AlmPublisher.AlmRep())
                .get();

        return new ToolbarRep(u, almRep);
    }
}
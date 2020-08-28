package gov.goias.smartapi.representation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Breno Olimpio on 11/01/2017.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
public class Sessao {

    private Integer codigoOrgao;
    private String nomeOrgao;

    private Integer codigoOrgaolotacoes;

    private Integer identificadorUnidadeAdministrativa;
    private String UnidadeAdministrativa;

    private String usuario;
    private String nomeUsuario;
    private String usuarioCpf;

    private Integer qtdeLogin = 0;

    @Getter(value = AccessLevel.PRIVATE)
    @Setter(value = AccessLevel.PRIVATE)
    private Map<Integer, String> mapaNomeLotacoes;

    public Sessao() {
        mapaNomeLotacoes = new HashMap<>();
    }

    public void adicionarLotacoes(Map<Integer, String> map) {
        this.mapaNomeLotacoes = map;
    }

    public void adicioanrUnidade(Integer codigo, String nome) {
        this.mapaNomeLotacoes.put(codigo, nome);
    }

    public String obterUnidade(Integer i) {
        return this.mapaNomeLotacoes.get(i);
    }

}
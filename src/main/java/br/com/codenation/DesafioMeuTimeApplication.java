package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import br.com.codenation.model.Jogador;
import br.com.codenation.model.Time;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

	private List<Time> times;
	private List<Jogador> jogadores;

	public DesafioMeuTimeApplication() {
		times = new ArrayList<>();
		jogadores = new ArrayList<>();
	}

	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal, String corUniformeSecundario) {
		if(timeExiste(id)){
			throw new IdentificadorUtilizadoException();
		}
		Time time = new Time(id, nome, dataCriacao ,corUniformePrincipal, corUniformeSecundario);
		times.add(time);
	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {
		if(jogadorExiste(id)){
			throw new IdentificadorUtilizadoException();
		}

		if(!timeExiste(idTime)){
			throw new TimeNaoEncontradoException();
		}

		Jogador jogador = new Jogador(id, idTime, nome, dataNascimento, nivelHabilidade, salario, false);
		jogadores.add(jogador);
	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {
		jogadores.stream()
				.filter(x -> x.getId().equals(idJogador))
				.findFirst().orElseThrow(JogadorNaoEncontradoException::new).setCapitao(true);

		jogadores.stream()
				.filter(j -> j.getIdTime().equals(jogadores.stream()
						.filter(x -> x.getId().equals(idJogador))
						.findFirst()
						.orElseThrow(JogadorNaoEncontradoException::new)
						.getIdTime()) & !j.getId().equals(idJogador))
				.forEach(j -> j.setCapitao(false));

	}

	public boolean jogadorExiste(Long idJogador){
		return jogadores.stream().anyMatch(x-> x.getId().equals(idJogador));
	}

	public boolean timeExiste(Long idTime){
		return times.stream().anyMatch(x-> x.getId().equals(idTime));
	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {
		if(!timeExiste(idTime)){
			throw new TimeNaoEncontradoException();
		}

		return jogadores.stream()
				.filter(x -> x.getIdTime().equals(idTime) & x.isCapitao())
				.findFirst().orElseThrow(CapitaoNaoInformadoException::new).getId();
	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {
		return jogadores.stream()
				.filter(x -> x.getId().equals(idJogador))
				.findFirst().orElseThrow(JogadorNaoEncontradoException::new).getNome();
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {
		return times.stream()
				.filter(x -> x.getId().equals(idTime))
				.findFirst().orElseThrow(TimeNaoEncontradoException::new).getNome();
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {
		if(!timeExiste(idTime)){
			throw new TimeNaoEncontradoException();
		}

		return jogadores.stream()
				.filter(x -> x.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getId))
				.map(x -> x.getId())
				.collect(Collectors.toList());
	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {
		if(!timeExiste(idTime)){
			throw new TimeNaoEncontradoException();
		}

		return jogadores.stream()
				.filter(x -> x.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getId))
				.max(Comparator.comparing(Jogador::getNivelHabilidade))
				.orElseThrow(NoSuchElementException::new).getId();
	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {
		if(!timeExiste(idTime)){
			throw new TimeNaoEncontradoException();
		}

		return jogadores.stream()
				.filter( j -> j.getIdTime().equals(idTime))
				.min(Comparator.comparing(Jogador::getDataNascimento))
				.orElseThrow(NoSuchElementException::new).getId();
	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {
		return times.stream()
				.sorted(Comparator.comparing(Time::getId))
				.map(x -> x.getId())
				.collect(Collectors.toList());
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {
		if(!timeExiste(idTime)){
			throw new TimeNaoEncontradoException();
		}

		return jogadores.stream()
				.filter( j -> j.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getId))
				.max(Comparator.comparing(Jogador::getSalario))
				.orElseThrow(NoSuchElementException::new).getId();
	}

	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {
		return jogadores.stream()
				.filter(x -> x.getId().equals(idJogador))
				.findFirst().orElseThrow(JogadorNaoEncontradoException::new).getSalario();
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {
		return jogadores.stream()
				.sorted(Comparator.comparing(Jogador::getNivelHabilidade)
						.reversed())
				.limit(top)
				.sorted(Comparator.comparing(Jogador::getId))
				.map(x -> x.getId())
				.collect(Collectors.toList());
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {
		if(!timeExiste(timeDaCasa) || (!timeExiste(timeDeFora) )){
			throw new TimeNaoEncontradoException();
		}

		String corCamisaPrincipalTimeDaCasa  = times.stream()
				.filter(x -> x.getId().equals(timeDaCasa))
				.findFirst().get().getCorUniformePrincipal();

		return times.stream()
				.filter(x -> x.getId().equals(timeDeFora))
				.map(x -> {
					if (x.getCorUniformePrincipal().equals(corCamisaPrincipalTimeDaCasa)) {
						return x.getCorUniformeSecundario();
					} else {
						return x.getCorUniformePrincipal();
					}
				})
				.findFirst().get();
	}

}

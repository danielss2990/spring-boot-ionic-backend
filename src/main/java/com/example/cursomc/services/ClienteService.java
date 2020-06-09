package com.example.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.cursomc.domain.Cidade;
import com.example.cursomc.domain.Cliente;
import com.example.cursomc.domain.Endereco;
import com.example.cursomc.domain.enums.TipoCliente;
import com.example.cursomc.dto.ClienteDTO;
import com.example.cursomc.dto.ClienteNewDTO;
import com.example.cursomc.repositories.ClienteRepository;
import com.example.cursomc.repositories.EnderecoRepository;
import com.example.cursomc.services.exceptions.DataIntegrityException;
import com.example.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	
	public Cliente find(Integer id) {
		
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj ;
	}
	
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um cliente que possui pedidos");
		}
		
	}
	
	public java.util.List<Cliente> findAll(){
		return repo.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction ){
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction),orderBy);
		return repo.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objetoDto) {
		return new Cliente(objetoDto.getId(), objetoDto.getNome(), objetoDto.getEmail(),null,null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objetoDto) {
		Cliente cli = new Cliente(null, objetoDto.getNome(), objetoDto.getEmail(),objetoDto.getCpfOuCnpj(),TipoCliente.toEnum(objetoDto.getTipo()));
		Cidade cid = new Cidade(objetoDto.getCidadeId(),null, null);
		Endereco end = new Endereco(null, objetoDto.getLogradouro(), objetoDto.getNumero(), objetoDto.getComplemento(), objetoDto.getBairro(),objetoDto.getCep(),cli,cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objetoDto.getTelefone1());
		
		if(objetoDto.getTelefone2() != null) {
			cli.getTelefones().add(objetoDto.getTelefone2());
		}
		
		if(objetoDto.getTelefone3() != null) {
			cli.getTelefones().add(objetoDto.getTelefone3());
		}
		
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	

}

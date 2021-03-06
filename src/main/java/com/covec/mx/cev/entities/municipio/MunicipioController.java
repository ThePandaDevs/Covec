package com.covec.mx.cev.entities.municipio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.covec.mx.cev.entities.operacion.OperacionService;
import com.covec.mx.cev.entities.usuario.Usuario;

@Controller
@RequestMapping("/municipios")
public class MunicipioController {

    @Autowired
    private MunicipioService municipioService;

    @Autowired
    private OperacionService operacionService;

    @GetMapping("/all")
    public String allMunicipios(@RequestParam Map<String,Object> params, Model model){
        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) - 1) : 0;
        PageRequest pageRequest = PageRequest.of(page,5);
        Page<Municipio> municipioPage = municipioService.getAll(pageRequest);
        int totalPages = municipioPage.getTotalPages();
        if (totalPages>0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("paginas",pages);
        }
        model.addAttribute("municipio",new Municipio());
        model.addAttribute("municipios",municipioPage.getContent());
        return "municipio/municipiocrud";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("municipio") Municipio municipio, BindingResult result, RedirectAttributes attributes, HttpSession httpSession){
        Usuario sesion = (Usuario) httpSession.getAttribute("user");
        if(result.hasErrors()){
            List<String> errores = new ArrayList<>();
            for (ObjectError error:result.getAllErrors()) {
                errores.add(error.getDefaultMessage());
            }
            attributes.addFlashAttribute("errores", errores);
        }else {
            municipioService.save(municipio);
            operacionService.guardarOperacion("Insert", sesion.getId(), "municipio:{nombre:'Sin registro previo'}", "municipio:{nombre:'"+municipio.getNombre()+"'}");
            attributes.addFlashAttribute("mensaje", "Se ha registrado correctamente");
        }
        return "redirect:/municipios/all";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("municipio") Municipio municipio, BindingResult result, RedirectAttributes attributes, HttpSession httpSession){
        Usuario sesion = (Usuario) httpSession.getAttribute("user");
        if(result.hasErrors()){
            List<String> errores = new ArrayList<>();
            for (ObjectError error:result.getAllErrors()) {
                errores.add(error.getDefaultMessage());
            }
            attributes.addFlashAttribute("errores", errores);
        }else {
            Municipio old = municipioService.getOne(municipio.getId());
            operacionService.guardarOperacion("Update", sesion.getId(), "municipio:{nombre:'"+old.getNombre()+"'}", "municipio:{nombre:'"+municipio.getNombre()+"'}");
            municipioService.update(municipio);
            attributes.addFlashAttribute("mensaje", "Se ha actualizado correctamente");
        }        
        return "redirect:/municipios/all";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession httpSession){
        Usuario sesion = (Usuario) httpSession.getAttribute("user");
        Municipio old = municipioService.getOne(id);
            operacionService.guardarOperacion("Delete", sesion.getId(), "municipio:{nombre:'"+old.getNombre()+"'}", "municipio:{nombre:'Se elimino el registro'");
        municipioService.delete(id);
        return "redirect:/municipios/all";
    }
}

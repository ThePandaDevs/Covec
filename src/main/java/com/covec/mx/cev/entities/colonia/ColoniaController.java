package com.covec.mx.cev.entities.colonia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.covec.mx.cev.entities.municipio.MunicipioService;
import com.covec.mx.cev.entities.operacion.OperacionService;
import com.covec.mx.cev.entities.usuario.enlace.Enlace;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/colonias")
public class ColoniaController {

    @Autowired
    private ColoniaService coloniaService;

    @Autowired
    private MunicipioService municipioService;

    @Autowired
    private OperacionService operacionService;  

    @GetMapping("/listar")
    public String getAll(@RequestParam Map<String,Object> params,HttpSession httpSession, Model model){
        Enlace enlaceSession = (Enlace) httpSession.getAttribute("user");
        //Obtenemos el contexto(número) actual de la vista(página)
        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) - 1) : 0;

        //Mandamos una consulta al repositorio en base al número actual(0) de la vista (0-5), numeramos de 5 en 5
        //(0-5) (5-10) (10-15)
        Page<Colonia> coloniaPages = coloniaService.getAllByMunicipio(enlaceSession.getMunicipio().getId(),PageRequest.of(page,5));
    
        if(coloniaPages.getTotalPages()>0){
            List<Integer> pages = IntStream.rangeClosed(1,coloniaPages.getTotalPages()).boxed().collect(Collectors.toList());
            model.addAttribute("paginas",pages);
        }
        model.addAttribute("municipio",enlaceSession.getMunicipio().getNombre());
        model.addAttribute("colonia",new Colonia());
        model.addAttribute("colonias",coloniaPages.getContent());
        return "colonia/coloniacrud";
    }

    @PostMapping("/guardar")
    public String save(@Valid @ModelAttribute("colonia") Colonia colonia, BindingResult result, RedirectAttributes attributes, HttpSession httpSession){
        Enlace enlaceSession = (Enlace) httpSession.getAttribute("user");
        colonia.setMunicipio(municipioService.getOne(enlaceSession.getMunicipio().getId()));
        if (result.hasErrors()){
            List<String> errores = new ArrayList<>();
            for (ObjectError error:result.getAllErrors()) {
                errores.add(error.getDefaultMessage());
            }
            attributes.addFlashAttribute("errores", errores);
        }else {
            coloniaService.save(colonia);
            operacionService.guardarOperacion("Insert", enlaceSession.getId(),"Sin datos previos", colonia.toStringColonia());
            attributes.addFlashAttribute("mensaje", "Se ha registrado correctamente");
        }
        return"redirect:/colonias/listar";
    }
 
    @PostMapping("/actualizar")
    public String update(@ModelAttribute("colonia") Colonia colonia, HttpSession httpSession){
        Enlace enlaceSession = (Enlace) httpSession.getAttribute("user");
        colonia.setMunicipio(enlaceSession.getMunicipio());
        Colonia anterior = coloniaService.getOne(colonia.getId());
        operacionService.guardarOperacion("Update", enlaceSession.getId(),anterior.toStringColonia(), colonia.toStringColonia());
        coloniaService.update(colonia);
        return "redirect:/colonias/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable Integer id,HttpSession httpSession){
        Enlace enlaceSession = (Enlace) httpSession.getAttribute("user");
        Colonia anterior = coloniaService.getOne(id);
        coloniaService.delete(id);
        operacionService.guardarOperacion("Delete", enlaceSession.getId(), anterior.toStringColonia(), "Sin datos actuales");
        return"redirect:/colonias/listar";
    }

}

package com.covec.mx.cev.entities.colonia;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/colonias")
public class ColoniaController {

    @Autowired
    private ColoniaService coloniaService;

    @GetMapping("/listar")
    public String getAll(@RequestParam Map<String,Object> params, Model model){
        
        //Obtenemos el contexto(número) actual de la vista(página)
        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) - 1) : 0;

        //Mandamos una consulta al repositorio en base al número actual(0) de la vista (0-5), numeramos de 5 en 5
        //(0-5) (5-10) (10-15)
        Page<Colonia> coloniaPages = coloniaService.getAll(PageRequest.of(page,5));
    
        if(coloniaPages.getTotalPages()>0){
            List<Integer> pages = IntStream.rangeClosed(1,coloniaPages.getTotalPages()).boxed().collect(Collectors.toList());
            model.addAttribute("paginas",pages);
        }
        model.addAttribute("colonia",new Colonia());
        model.addAttribute("colonias",coloniaPages.getContent());
        return "colonia/coloniacrud";
    }

    @PostMapping("/guardar")
    public String save(){
        return"redirect:/listar";
    }
 
    @PatchMapping("/actualizar")
    public String update(){
        return "redirect:/listar";
    }

    @DeleteMapping
    public String delete(){
        return"redirect:/listar";
    }

}

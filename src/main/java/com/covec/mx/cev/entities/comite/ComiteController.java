package com.covec.mx.cev.entities.comite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.*;
import javax.validation.Valid;

import com.covec.mx.cev.entities.colonia.Colonia;
import com.covec.mx.cev.entities.colonia.ColoniaService;
import com.covec.mx.cev.entities.rol.Rol;
import com.covec.mx.cev.entities.rol.RolService;
import com.covec.mx.cev.entities.usuario.integrante.Integrante;
import com.covec.mx.cev.entities.usuario.integrante.IntegranteService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@Controller
@RequestMapping("/comites")
public class ComiteController {

    @Autowired
    private ComiteService comiteService;

    @Autowired
    private ColoniaService coloniaService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private IntegranteService integranteService;

    @Autowired
    private RolService rolService;

    private List<Integer>listIds = new LinkedList<>(); 
    private List<Integrante> integrantes = new LinkedList<>();
    private static final String CONST_COMITE ="comite";
    private static final String CONST_INTEGRANTE="integrante";
    private static final String CONST_INTEGRANTES="integrantes";
    private Comite comite = new Comite();
    private Integer coloniaID;
    

    @GetMapping("/listar/{id}")
    public String getAll(Model model, @PathVariable("id") Integer id, @RequestParam Map<String, Object> params) {
        // Obtenemos el contexto(número) actual de la vista(página)
        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) - 1) : 0;

        // Mandamos una consulta al repositorio en base al número actual(0) de la vista
        // (0-5), numeramos de 5 en 5
        // (0-5) (5-10) (10-15)
        Page<Comite> comitePages = comiteService.getAll(id,PageRequest.of(page, 5));

        if (comitePages.getTotalPages() > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, comitePages.getTotalPages()).boxed()
                    .collect(Collectors.toList());
            model.addAttribute("paginas", pages);
        }
        coloniaID = id;
        listIds.clear();
        integrantes.clear();
        model.addAttribute("colonia", coloniaService.getOne(id));
        model.addAttribute(CONST_COMITE, new Comite());
        model.addAttribute("comites", comitePages.getContent());
        return "comite/comitecrud";
    }
    
    @GetMapping("/integrantes/listar/{id}")
    public String seeMembers(@PathVariable("id") Integer id, Model model){
        comite = comiteService.getOne(id);
        integrantes = comite.getIntegrantes();
        model.addAttribute(CONST_INTEGRANTE, new Integrante());
        model.addAttribute(CONST_INTEGRANTES,integrantes);
        model.addAttribute(CONST_COMITE,comite);
        return "comite/integrantecrud";
    }

    @GetMapping("/integrantes/listar/refrescar")
    public String seeUpdatedMembers(Model model){
        model.addAttribute(CONST_INTEGRANTE, new Integrante());
        model.addAttribute(CONST_INTEGRANTES,integrantes);
        model.addAttribute(CONST_COMITE,comite);
        return "comite/integrantecrud";
    }


    /**
     * Vista para un nuevo comite 
     */
    @GetMapping("/integrantes/nuevo")
    public String newGroup(Model model){
        model.addAttribute("ID",coloniaID);
        model.addAttribute(CONST_INTEGRANTE, new Integrante());
        return "comite/crearcomite";
    }

    /**
     * Método para crear un integrante | enlace
     */
    @PostMapping("/integrantes/agregar")
    public String add(@Valid Integrante integrante, BindingResult result, RedirectAttributes attributes, Model model){
        Colonia colonia = coloniaService.getOne(coloniaID); 
        if (result.hasErrors()){
            List<String> errores = new ArrayList<>();
            for (ObjectError error:result.getAllErrors()) {
                errores.add(error.getDefaultMessage());
            }
            attributes.addFlashAttribute("errores", errores);
        }else {
            Comite nuevoComite = new Comite();
            integrante.setComite(nuevoComite);
            integrante.setEnabled(true);
            integrante.setTipoUsuario("Presidente");
            integrante.setPresidente(true);
            String encode = passwordEncoder.encode(integrante.getPassword());
            integrante.setPassword(encode);
            Rol rolUsuario = rolService.getOne(3);
            Set<Rol> roles = new HashSet<>();
            roles.add(rolUsuario);
            integrante.setRoles(roles);

            integrantes.add(integrante);
            
            nuevoComite.setIntegrantes(integrantes);
            nuevoComite.setColonia(colonia);
            nuevoComite.setActivo(false);
            comiteService.save(nuevoComite);
            attributes.addFlashAttribute("mensaje", "Se ha agregado correctamente");
        }
        return "redirect:/comites/listar/"+coloniaID;
    }
    
    @GetMapping("/integrantes/quitar/{telefono}")
    public String delete(Model model, @PathVariable("telefono") String telefono){
        integrantes.removeIf(i -> (i.getTelefono().equals(telefono)));
        return "redirect:/comites/integrantes/nuevo";
    }

    @GetMapping("/integrantes/existente/nuevo")
    public String existingGroup(Model model){
        model.addAttribute(CONST_COMITE,comite);
        model.addAttribute("ID",coloniaID);
        model.addAttribute(CONST_INTEGRANTE, new Integrante());
        model.addAttribute(CONST_INTEGRANTES,integrantes);
        return "comite/integrantecrud";
    }

    @PostMapping("/integrantes/existente/agregar")
    public String addExisting(Model model,Integrante integrante){
        integrante.setComite(comite);
        integrantes.add(integrante);
        return "redirect:/comites/integrantes/existente/nuevo";
    }

    @GetMapping("/integrantes/existente/quitar/{telefono}")
    public String deleteExisting(Model model, @PathVariable("telefono") String telefono){
        for (Integrante integrante : integrantes) {
            if(integrante.getId()!=null && integrante.getTelefono().equals(telefono)){
                listIds.add(integrante.getId()) ;
            }
        }
        integrantes.removeIf(i -> (i.getTelefono().equals(telefono)));
        return "redirect:/comites/integrantes/existente/nuevo";
    }

    @PostMapping("/integrantes/existente/actualizar")
    public String updateExisting(@ModelAttribute("integrante")Integrante integrante){
        for (Integrante i : integrantes) {
            if(i.getId().equals(integrante.getId())){
                BeanUtils.copyProperties(integrante, i);
                i.setComite(comite);
                if (i.getPresidente() == null){
                    i.setPresidente(false);
                }
                break;
            }
        }
        return"redirect:/comites/integrantes/listar/refrescar";
    }

    @GetMapping("/guardar/{id}")
    public String save(@PathVariable("id") Integer id) {
        Colonia colonia = coloniaService.getOne(coloniaID);
        comite.setIntegrantes(integrantes);
        comite.setColonia(colonia);
        comiteService.save(comite);
        return "redirect:/comites/listar/"+id;
    }

    @GetMapping("/actualizar")
    public String update() {
        integranteService.deleteMultple(listIds);
        Colonia colonia = coloniaService.getOne(coloniaID);
        comite.setIntegrantes(integrantes);
        comite.setColonia(colonia);
        comiteService.save(comite);
        return "redirect:/comites/listar/"+coloniaID;
    }

    @GetMapping("/eliminar/{id}")
    public String delete(@PathVariable("id") Integer id) {
        comiteService.delete(id);
        return "redirect:/comites/listar/"+coloniaID;
    }
}

package com.backend.controller;

import com.backend.dto.ComplaintDTO;
import com.backend.dto.EntityDTO;
import com.backend.dto.SaleDTO;
import com.backend.model.*;
import com.backend.service.Base64ToImage;
import com.backend.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/entity")
public class EntityController {

    @Autowired
    EntityService entityService;

    private Base64ToImage base64ToImage = new Base64ToImage();

    @GetMapping(value="{state}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<EntityDTO>> getAllEntities(@PathVariable int state) throws IOException {
        Collection<? extends RentingEntity> entities= entityService.GetAllEntities(state);

        Collection<EntityDTO> dto = getEntityDTOS(entities);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping(value = "/subscriptions",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<EntityDTO>> getSubscriptions(Principal principal) throws IOException {
        Collection<? extends RentingEntity> entities= entityService.GetByUsersSubscriptions(principal.getName());

        Collection<EntityDTO> dto = getEntityDTOS(entities);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private Collection<EntityDTO> getEntityDTOS(Collection<? extends RentingEntity> entities) throws IOException {
        Collection<EntityDTO> dto = new ArrayList<>();
        String type;
        for (RentingEntity e : entities) {
            if(e.getClass().equals(Adventure.class)) type="Adventure";
            else if(e.getClass().equals(Cottage.class)) type="Cottage";
            else type = "Ship";

            String[] images = e.getImages().toArray(new String[e.getImages().size()]);
            EntityDTO entityDTO;
            if(images.length > 0) {
                entityDTO = new EntityDTO(e.getId(), e.getName(), e.getDescription(), e.getAverageGrade(), base64ToImage.encodeImageToBase64(images[0]), e.getAddress(), type);
            } else {
                entityDTO = new EntityDTO(e.getId(), e.getName(), e.getDescription(), e.getAverageGrade(), base64ToImage.encodeImageToBase64("/images/undefined/no_image.jpg"), e.getAddress(),type);
            }
            dto.add(entityDTO);
        }
        return dto;
    }

    @DeleteMapping ("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COTTAGE_OWNER', 'SHIP_OWNER', 'INSTRUCTOR')")
    public ResponseEntity<?> deleteEntityById(@PathVariable("id") Integer id) {
        entityService.deleteEntity(id);
        return new ResponseEntity<>("Entity deleted!", HttpStatus.OK);
    }

    @GetMapping("/check-subscription/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Boolean> checkIfSubscribed(@PathVariable Integer id, Principal principal) {
        return new ResponseEntity<>(entityService.checkIfSubscribed(principal.getName(), id), HttpStatus.OK);
    }

    @GetMapping(value = "/version/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getVersion(@PathVariable Integer id) {
        return new ResponseEntity<>(entityService.getEntityById(id).getVersion(), HttpStatus.OK);
    }

    @GetMapping(value="/get-on-sale/{state}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Collection<EntityDTO>>getEntitiesOnSale(@PathVariable int state) throws IOException {
        Collection<? extends RentingEntity> entities=entityService.getEntitiesOnSale(state);
        Collection<EntityDTO> dto = getEntityDTOS(entities);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value="/get-available-for-period/{state}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Collection<EntityDTO>>getAvailableEntities(@RequestBody UnavailablePeriod period,@PathVariable int state) throws IOException {
        Collection<? extends RentingEntity> entities=entityService.getAvailableEntities(period,state);
        Collection<EntityDTO> dto = getEntityDTOS(entities);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}

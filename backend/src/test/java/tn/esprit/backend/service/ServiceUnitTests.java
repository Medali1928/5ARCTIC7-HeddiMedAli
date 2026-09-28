package tn.esprit.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.EntrepriseServiceImpl;
import tn.esprit.backend.service.impl.EquipeServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires des services (Mockito) — aucune base de données requise.
 */
@ExtendWith(MockitoExtension.class)
class ServiceUnitTests {

    @Mock EntrepriseRepository entrepriseRepository;
    @Mock EquipeRepository equipeRepository;
    @Mock ProjetRepository projetRepository;

    @InjectMocks EntrepriseServiceImpl entrepriseService;
    @InjectMocks EquipeServiceImpl equipeService;

    // 1
    @Test
    void addEntreprise_doitSauvegarderEtRetournerEntreprise() {
        Entreprise e = Entreprise.builder().nom("Esprit").adresse("Tunis").build();
        when(entrepriseRepository.save(e)).thenReturn(e);

        Entreprise result = entrepriseService.addEntreprise(e);

        assertEquals("Esprit", result.getNom());
        verify(entrepriseRepository, times(1)).save(e);
    }

    // 2
    @Test
    void getEntrepriseById_doitRetournerEntrepriseExistante() {
        Entreprise e = Entreprise.builder().id(1L).nom("Esprit").build();
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(e));

        Entreprise result = entrepriseService.getEntrepriseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // 3
    @Test
    void getEntrepriseById_doitRetournerNullSiInexistante() {
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(entrepriseService.getEntrepriseById(99L));
    }

    // 4
    @Test
    void getAllEntreprises_doitRetournerLaListe() {
        when(entrepriseRepository.findAll()).thenReturn(List.of(
                Entreprise.builder().nom("A").build(),
                Entreprise.builder().nom("B").build()));

        assertEquals(2, entrepriseService.getAllEntreprises().size());
    }

    // 5
    @Test
    void assignEquipeToEntreprise_doitLierEquipeEtEntreprise() {
        Equipe equipe = Equipe.builder().id(1L).nom("DevOps").projets(new ArrayList<>()).build();
        Entreprise entreprise = Entreprise.builder().id(2L).nom("Esprit").build();
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(entrepriseRepository.findById(2L)).thenReturn(Optional.of(entreprise));
        when(equipeRepository.save(any(Equipe.class))).thenAnswer(inv -> inv.getArgument(0));

        Equipe result = equipeService.assignEquipeToEntreprise(1L, 2L);

        assertSame(entreprise, result.getEntreprise());
        verify(equipeRepository).save(equipe);
    }

    // 6 (bonus)
    @Test
    void deleteEntreprise_doitAppelerDeleteById() {
        entrepriseService.deleteEntreprise(5L);
        verify(entrepriseRepository, times(1)).deleteById(5L);
    }
}

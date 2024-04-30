package org.evote.backend.unit.services;

import org.evote.backend.services.PoliticalPartyService;
import org.evote.backend.votes.political_party.entity.PoliticalParty;
import org.evote.backend.votes.political_party.repository.PoliticalPartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PoliticalPartyServiceTests {

    @InjectMocks
    private PoliticalPartyService politicalPartyService;

    @Mock
    private PoliticalPartyRepository politicalPartyRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllPoliticalParties() {
        PoliticalParty politicalParty1 = new PoliticalParty();
        PoliticalParty politicalParty2 = new PoliticalParty();
        List<PoliticalParty> politicalParties = Arrays.asList(politicalParty1, politicalParty2);

        when(politicalPartyRepository.findAll()).thenReturn(politicalParties);

        List<PoliticalParty> result = politicalPartyService.getAllPoliticalParties();

        assertEquals(politicalParties, result);
    }

    @Test
    public void testGetPoliticalPartyNameById() {
        Long id = 1L;
        String name = "Test Party";

        when(politicalPartyRepository.findNameById(Math.toIntExact(id))).thenReturn(name);

        String result = politicalPartyService.getPoliticalPartyNameById(id);

        assertEquals(name, result);
    }
}
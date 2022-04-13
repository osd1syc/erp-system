package io.github.erp.erp.resources;

import static io.github.erp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.erp.IntegrationTest;
import io.github.erp.domain.Dealer;
import io.github.erp.domain.DeliveryNote;
import io.github.erp.domain.JobSheet;
import io.github.erp.domain.PaymentInvoice;
import io.github.erp.domain.Placeholder;
import io.github.erp.domain.PurchaseOrder;
import io.github.erp.domain.ServiceOutlet;
import io.github.erp.domain.Settlement;
import io.github.erp.domain.WorkInProgressRegistration;
import io.github.erp.erp.resources.WorkInProgressRegistrationResource;
import io.github.erp.repository.WorkInProgressRegistrationRepository;
import io.github.erp.repository.search.WorkInProgressRegistrationSearchRepository;
import io.github.erp.service.WorkInProgressRegistrationService;
import io.github.erp.service.dto.WorkInProgressRegistrationDTO;
import io.github.erp.service.mapper.WorkInProgressRegistrationMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;

import io.github.erp.web.rest.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link WorkInProgressRegistrationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(roles = {"FIXED_ASSETS_USER"})
public class WorkInProgressRegistrationResourceIT {

    private static final String DEFAULT_SEQUENCE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SEQUENCE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PARTICULARS = "AAAAAAAAAA";
    private static final String UPDATED_PARTICULARS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_INSTALMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_INSTALMENT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_INSTALMENT_AMOUNT = new BigDecimal(1 - 1);

    private static final byte[] DEFAULT_COMMENTS = io.github.erp.web.rest.TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COMMENTS = io.github.erp.web.rest.TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COMMENTS_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COMMENTS_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/fixed-asset/work-in-progress-registrations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/fixed-asset/_search/work-in-progress-registrations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkInProgressRegistrationRepository workInProgressRegistrationRepository;

    @Mock
    private WorkInProgressRegistrationRepository workInProgressRegistrationRepositoryMock;

    @Autowired
    private WorkInProgressRegistrationMapper workInProgressRegistrationMapper;

    @Mock
    private WorkInProgressRegistrationService workInProgressRegistrationServiceMock;

    /**
     * This repository is mocked in the io.github.erp.repository.search test package.
     *
     * @see io.github.erp.repository.search.WorkInProgressRegistrationSearchRepositoryMockConfiguration
     */
    @Autowired
    private WorkInProgressRegistrationSearchRepository mockWorkInProgressRegistrationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkInProgressRegistrationMockMvc;

    private WorkInProgressRegistration workInProgressRegistration;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkInProgressRegistration createEntity(EntityManager em) {
        WorkInProgressRegistration workInProgressRegistration = new WorkInProgressRegistration()
            .sequenceNumber(DEFAULT_SEQUENCE_NUMBER)
            .particulars(DEFAULT_PARTICULARS)
            .instalmentAmount(DEFAULT_INSTALMENT_AMOUNT)
            .comments(DEFAULT_COMMENTS)
            .commentsContentType(DEFAULT_COMMENTS_CONTENT_TYPE);
        // Add required entity
        ServiceOutlet serviceOutlet;
        if (io.github.erp.web.rest.TestUtil.findAll(em, ServiceOutlet.class).isEmpty()) {
            serviceOutlet = ServiceOutletResourceIT.createEntity(em);
            em.persist(serviceOutlet);
            em.flush();
        } else {
            serviceOutlet = io.github.erp.web.rest.TestUtil.findAll(em, ServiceOutlet.class).get(0);
        }
        workInProgressRegistration.getServiceOutlets().add(serviceOutlet);
        // Add required entity
        Settlement settlement;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Settlement.class).isEmpty()) {
            settlement = SettlementResourceIT.createEntity(em);
            em.persist(settlement);
            em.flush();
        } else {
            settlement = io.github.erp.web.rest.TestUtil.findAll(em, Settlement.class).get(0);
        }
        workInProgressRegistration.getSettlements().add(settlement);
        // Add required entity
        Dealer dealer;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = io.github.erp.web.rest.TestUtil.findAll(em, Dealer.class).get(0);
        }
        workInProgressRegistration.setDealer(dealer);
        return workInProgressRegistration;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkInProgressRegistration createUpdatedEntity(EntityManager em) {
        WorkInProgressRegistration workInProgressRegistration = new WorkInProgressRegistration()
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .particulars(UPDATED_PARTICULARS)
            .instalmentAmount(UPDATED_INSTALMENT_AMOUNT)
            .comments(UPDATED_COMMENTS)
            .commentsContentType(UPDATED_COMMENTS_CONTENT_TYPE);
        // Add required entity
        ServiceOutlet serviceOutlet;
        if (io.github.erp.web.rest.TestUtil.findAll(em, ServiceOutlet.class).isEmpty()) {
            serviceOutlet = ServiceOutletResourceIT.createUpdatedEntity(em);
            em.persist(serviceOutlet);
            em.flush();
        } else {
            serviceOutlet = io.github.erp.web.rest.TestUtil.findAll(em, ServiceOutlet.class).get(0);
        }
        workInProgressRegistration.getServiceOutlets().add(serviceOutlet);
        // Add required entity
        Settlement settlement;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Settlement.class).isEmpty()) {
            settlement = SettlementResourceIT.createUpdatedEntity(em);
            em.persist(settlement);
            em.flush();
        } else {
            settlement = io.github.erp.web.rest.TestUtil.findAll(em, Settlement.class).get(0);
        }
        workInProgressRegistration.getSettlements().add(settlement);
        // Add required entity
        Dealer dealer;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createUpdatedEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = io.github.erp.web.rest.TestUtil.findAll(em, Dealer.class).get(0);
        }
        workInProgressRegistration.setDealer(dealer);
        return workInProgressRegistration;
    }

    @BeforeEach
    public void initTest() {
        workInProgressRegistration = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeCreate = workInProgressRegistrationRepository.findAll().size();
        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);
        restWorkInProgressRegistrationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeCreate + 1);
        WorkInProgressRegistration testWorkInProgressRegistration = workInProgressRegistrationList.get(
            workInProgressRegistrationList.size() - 1
        );
        assertThat(testWorkInProgressRegistration.getSequenceNumber()).isEqualTo(DEFAULT_SEQUENCE_NUMBER);
        assertThat(testWorkInProgressRegistration.getParticulars()).isEqualTo(DEFAULT_PARTICULARS);
        assertThat(testWorkInProgressRegistration.getInstalmentAmount()).isEqualByComparingTo(DEFAULT_INSTALMENT_AMOUNT);
        assertThat(testWorkInProgressRegistration.getComments()).isEqualTo(DEFAULT_COMMENTS);
        assertThat(testWorkInProgressRegistration.getCommentsContentType()).isEqualTo(DEFAULT_COMMENTS_CONTENT_TYPE);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(1)).save(testWorkInProgressRegistration);
    }

    @Test
    @Transactional
    void createWorkInProgressRegistrationWithExistingId() throws Exception {
        // Create the WorkInProgressRegistration with an existing ID
        workInProgressRegistration.setId(1L);
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        int databaseSizeBeforeCreate = workInProgressRegistrationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkInProgressRegistrationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeCreate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void checkSequenceNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInProgressRegistrationRepository.findAll().size();
        // set the field null
        workInProgressRegistration.setSequenceNumber(null);

        // Create the WorkInProgressRegistration, which fails.
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        restWorkInProgressRegistrationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrations() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workInProgressRegistration.getId().intValue())))
            .andExpect(jsonPath("$.[*].sequenceNumber").value(hasItem(DEFAULT_SEQUENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].particulars").value(hasItem(DEFAULT_PARTICULARS)))
            .andExpect(jsonPath("$.[*].instalmentAmount").value(hasItem(sameNumber(DEFAULT_INSTALMENT_AMOUNT))))
            .andExpect(jsonPath("$.[*].commentsContentType").value(hasItem(DEFAULT_COMMENTS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(Base64Utils.encodeToString(DEFAULT_COMMENTS))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkInProgressRegistrationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(workInProgressRegistrationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkInProgressRegistrationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workInProgressRegistrationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkInProgressRegistrationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(workInProgressRegistrationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkInProgressRegistrationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workInProgressRegistrationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getWorkInProgressRegistration() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get the workInProgressRegistration
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_API_URL_ID, workInProgressRegistration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workInProgressRegistration.getId().intValue()))
            .andExpect(jsonPath("$.sequenceNumber").value(DEFAULT_SEQUENCE_NUMBER))
            .andExpect(jsonPath("$.particulars").value(DEFAULT_PARTICULARS))
            .andExpect(jsonPath("$.instalmentAmount").value(sameNumber(DEFAULT_INSTALMENT_AMOUNT)))
            .andExpect(jsonPath("$.commentsContentType").value(DEFAULT_COMMENTS_CONTENT_TYPE))
            .andExpect(jsonPath("$.comments").value(Base64Utils.encodeToString(DEFAULT_COMMENTS)));
    }

    @Test
    @Transactional
    void getWorkInProgressRegistrationsByIdFiltering() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        Long id = workInProgressRegistration.getId();

        defaultWorkInProgressRegistrationShouldBeFound("id.equals=" + id);
        defaultWorkInProgressRegistrationShouldNotBeFound("id.notEquals=" + id);

        defaultWorkInProgressRegistrationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkInProgressRegistrationShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkInProgressRegistrationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkInProgressRegistrationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySequenceNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where sequenceNumber equals to DEFAULT_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldBeFound("sequenceNumber.equals=" + DEFAULT_SEQUENCE_NUMBER);

        // Get all the workInProgressRegistrationList where sequenceNumber equals to UPDATED_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldNotBeFound("sequenceNumber.equals=" + UPDATED_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySequenceNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where sequenceNumber not equals to DEFAULT_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldNotBeFound("sequenceNumber.notEquals=" + DEFAULT_SEQUENCE_NUMBER);

        // Get all the workInProgressRegistrationList where sequenceNumber not equals to UPDATED_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldBeFound("sequenceNumber.notEquals=" + UPDATED_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySequenceNumberIsInShouldWork() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where sequenceNumber in DEFAULT_SEQUENCE_NUMBER or UPDATED_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldBeFound("sequenceNumber.in=" + DEFAULT_SEQUENCE_NUMBER + "," + UPDATED_SEQUENCE_NUMBER);

        // Get all the workInProgressRegistrationList where sequenceNumber equals to UPDATED_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldNotBeFound("sequenceNumber.in=" + UPDATED_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySequenceNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where sequenceNumber is not null
        defaultWorkInProgressRegistrationShouldBeFound("sequenceNumber.specified=true");

        // Get all the workInProgressRegistrationList where sequenceNumber is null
        defaultWorkInProgressRegistrationShouldNotBeFound("sequenceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySequenceNumberContainsSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where sequenceNumber contains DEFAULT_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldBeFound("sequenceNumber.contains=" + DEFAULT_SEQUENCE_NUMBER);

        // Get all the workInProgressRegistrationList where sequenceNumber contains UPDATED_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldNotBeFound("sequenceNumber.contains=" + UPDATED_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySequenceNumberNotContainsSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where sequenceNumber does not contain DEFAULT_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldNotBeFound("sequenceNumber.doesNotContain=" + DEFAULT_SEQUENCE_NUMBER);

        // Get all the workInProgressRegistrationList where sequenceNumber does not contain UPDATED_SEQUENCE_NUMBER
        defaultWorkInProgressRegistrationShouldBeFound("sequenceNumber.doesNotContain=" + UPDATED_SEQUENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByParticularsIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where particulars equals to DEFAULT_PARTICULARS
        defaultWorkInProgressRegistrationShouldBeFound("particulars.equals=" + DEFAULT_PARTICULARS);

        // Get all the workInProgressRegistrationList where particulars equals to UPDATED_PARTICULARS
        defaultWorkInProgressRegistrationShouldNotBeFound("particulars.equals=" + UPDATED_PARTICULARS);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByParticularsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where particulars not equals to DEFAULT_PARTICULARS
        defaultWorkInProgressRegistrationShouldNotBeFound("particulars.notEquals=" + DEFAULT_PARTICULARS);

        // Get all the workInProgressRegistrationList where particulars not equals to UPDATED_PARTICULARS
        defaultWorkInProgressRegistrationShouldBeFound("particulars.notEquals=" + UPDATED_PARTICULARS);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByParticularsIsInShouldWork() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where particulars in DEFAULT_PARTICULARS or UPDATED_PARTICULARS
        defaultWorkInProgressRegistrationShouldBeFound("particulars.in=" + DEFAULT_PARTICULARS + "," + UPDATED_PARTICULARS);

        // Get all the workInProgressRegistrationList where particulars equals to UPDATED_PARTICULARS
        defaultWorkInProgressRegistrationShouldNotBeFound("particulars.in=" + UPDATED_PARTICULARS);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByParticularsIsNullOrNotNull() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where particulars is not null
        defaultWorkInProgressRegistrationShouldBeFound("particulars.specified=true");

        // Get all the workInProgressRegistrationList where particulars is null
        defaultWorkInProgressRegistrationShouldNotBeFound("particulars.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByParticularsContainsSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where particulars contains DEFAULT_PARTICULARS
        defaultWorkInProgressRegistrationShouldBeFound("particulars.contains=" + DEFAULT_PARTICULARS);

        // Get all the workInProgressRegistrationList where particulars contains UPDATED_PARTICULARS
        defaultWorkInProgressRegistrationShouldNotBeFound("particulars.contains=" + UPDATED_PARTICULARS);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByParticularsNotContainsSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where particulars does not contain DEFAULT_PARTICULARS
        defaultWorkInProgressRegistrationShouldNotBeFound("particulars.doesNotContain=" + DEFAULT_PARTICULARS);

        // Get all the workInProgressRegistrationList where particulars does not contain UPDATED_PARTICULARS
        defaultWorkInProgressRegistrationShouldBeFound("particulars.doesNotContain=" + UPDATED_PARTICULARS);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount equals to DEFAULT_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.equals=" + DEFAULT_INSTALMENT_AMOUNT);

        // Get all the workInProgressRegistrationList where instalmentAmount equals to UPDATED_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.equals=" + UPDATED_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount not equals to DEFAULT_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.notEquals=" + DEFAULT_INSTALMENT_AMOUNT);

        // Get all the workInProgressRegistrationList where instalmentAmount not equals to UPDATED_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.notEquals=" + UPDATED_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsInShouldWork() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount in DEFAULT_INSTALMENT_AMOUNT or UPDATED_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound(
            "instalmentAmount.in=" + DEFAULT_INSTALMENT_AMOUNT + "," + UPDATED_INSTALMENT_AMOUNT
        );

        // Get all the workInProgressRegistrationList where instalmentAmount equals to UPDATED_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.in=" + UPDATED_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount is not null
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.specified=true");

        // Get all the workInProgressRegistrationList where instalmentAmount is null
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount is greater than or equal to DEFAULT_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.greaterThanOrEqual=" + DEFAULT_INSTALMENT_AMOUNT);

        // Get all the workInProgressRegistrationList where instalmentAmount is greater than or equal to UPDATED_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.greaterThanOrEqual=" + UPDATED_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount is less than or equal to DEFAULT_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.lessThanOrEqual=" + DEFAULT_INSTALMENT_AMOUNT);

        // Get all the workInProgressRegistrationList where instalmentAmount is less than or equal to SMALLER_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.lessThanOrEqual=" + SMALLER_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount is less than DEFAULT_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.lessThan=" + DEFAULT_INSTALMENT_AMOUNT);

        // Get all the workInProgressRegistrationList where instalmentAmount is less than UPDATED_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.lessThan=" + UPDATED_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByInstalmentAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        // Get all the workInProgressRegistrationList where instalmentAmount is greater than DEFAULT_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldNotBeFound("instalmentAmount.greaterThan=" + DEFAULT_INSTALMENT_AMOUNT);

        // Get all the workInProgressRegistrationList where instalmentAmount is greater than SMALLER_INSTALMENT_AMOUNT
        defaultWorkInProgressRegistrationShouldBeFound("instalmentAmount.greaterThan=" + SMALLER_INSTALMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByPlaceholderIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Placeholder placeholder;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Placeholder.class).isEmpty()) {
            placeholder = PlaceholderResourceIT.createEntity(em);
            em.persist(placeholder);
            em.flush();
        } else {
            placeholder = io.github.erp.web.rest.TestUtil.findAll(em, Placeholder.class).get(0);
        }
        em.persist(placeholder);
        em.flush();
        workInProgressRegistration.addPlaceholder(placeholder);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long placeholderId = placeholder.getId();

        // Get all the workInProgressRegistrationList where placeholder equals to placeholderId
        defaultWorkInProgressRegistrationShouldBeFound("placeholderId.equals=" + placeholderId);

        // Get all the workInProgressRegistrationList where placeholder equals to (placeholderId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("placeholderId.equals=" + (placeholderId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByPaymentInvoicesIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        PaymentInvoice paymentInvoices;
        if (io.github.erp.web.rest.TestUtil.findAll(em, PaymentInvoice.class).isEmpty()) {
            paymentInvoices = PaymentInvoiceResourceIT.createEntity(em);
            em.persist(paymentInvoices);
            em.flush();
        } else {
            paymentInvoices = io.github.erp.web.rest.TestUtil.findAll(em, PaymentInvoice.class).get(0);
        }
        em.persist(paymentInvoices);
        em.flush();
        workInProgressRegistration.addPaymentInvoices(paymentInvoices);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long paymentInvoicesId = paymentInvoices.getId();

        // Get all the workInProgressRegistrationList where paymentInvoices equals to paymentInvoicesId
        defaultWorkInProgressRegistrationShouldBeFound("paymentInvoicesId.equals=" + paymentInvoicesId);

        // Get all the workInProgressRegistrationList where paymentInvoices equals to (paymentInvoicesId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("paymentInvoicesId.equals=" + (paymentInvoicesId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByServiceOutletIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        ServiceOutlet serviceOutlet;
        if (io.github.erp.web.rest.TestUtil.findAll(em, ServiceOutlet.class).isEmpty()) {
            serviceOutlet = ServiceOutletResourceIT.createEntity(em);
            em.persist(serviceOutlet);
            em.flush();
        } else {
            serviceOutlet = io.github.erp.web.rest.TestUtil.findAll(em, ServiceOutlet.class).get(0);
        }
        em.persist(serviceOutlet);
        em.flush();
        workInProgressRegistration.addServiceOutlet(serviceOutlet);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long serviceOutletId = serviceOutlet.getId();

        // Get all the workInProgressRegistrationList where serviceOutlet equals to serviceOutletId
        defaultWorkInProgressRegistrationShouldBeFound("serviceOutletId.equals=" + serviceOutletId);

        // Get all the workInProgressRegistrationList where serviceOutlet equals to (serviceOutletId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("serviceOutletId.equals=" + (serviceOutletId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsBySettlementIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Settlement settlement;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Settlement.class).isEmpty()) {
            settlement = SettlementResourceIT.createEntity(em);
            em.persist(settlement);
            em.flush();
        } else {
            settlement = io.github.erp.web.rest.TestUtil.findAll(em, Settlement.class).get(0);
        }
        em.persist(settlement);
        em.flush();
        workInProgressRegistration.addSettlement(settlement);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long settlementId = settlement.getId();

        // Get all the workInProgressRegistrationList where settlement equals to settlementId
        defaultWorkInProgressRegistrationShouldBeFound("settlementId.equals=" + settlementId);

        // Get all the workInProgressRegistrationList where settlement equals to (settlementId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("settlementId.equals=" + (settlementId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByPurchaseOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        PurchaseOrder purchaseOrder;
        if (io.github.erp.web.rest.TestUtil.findAll(em, PurchaseOrder.class).isEmpty()) {
            purchaseOrder = PurchaseOrderResourceIT.createEntity(em);
            em.persist(purchaseOrder);
            em.flush();
        } else {
            purchaseOrder = io.github.erp.web.rest.TestUtil.findAll(em, PurchaseOrder.class).get(0);
        }
        em.persist(purchaseOrder);
        em.flush();
        workInProgressRegistration.addPurchaseOrder(purchaseOrder);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long purchaseOrderId = purchaseOrder.getId();

        // Get all the workInProgressRegistrationList where purchaseOrder equals to purchaseOrderId
        defaultWorkInProgressRegistrationShouldBeFound("purchaseOrderId.equals=" + purchaseOrderId);

        // Get all the workInProgressRegistrationList where purchaseOrder equals to (purchaseOrderId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("purchaseOrderId.equals=" + (purchaseOrderId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByDeliveryNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        DeliveryNote deliveryNote;
        if (io.github.erp.web.rest.TestUtil.findAll(em, DeliveryNote.class).isEmpty()) {
            deliveryNote = DeliveryNoteResourceIT.createEntity(em);
            em.persist(deliveryNote);
            em.flush();
        } else {
            deliveryNote = io.github.erp.web.rest.TestUtil.findAll(em, DeliveryNote.class).get(0);
        }
        em.persist(deliveryNote);
        em.flush();
        workInProgressRegistration.addDeliveryNote(deliveryNote);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long deliveryNoteId = deliveryNote.getId();

        // Get all the workInProgressRegistrationList where deliveryNote equals to deliveryNoteId
        defaultWorkInProgressRegistrationShouldBeFound("deliveryNoteId.equals=" + deliveryNoteId);

        // Get all the workInProgressRegistrationList where deliveryNote equals to (deliveryNoteId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("deliveryNoteId.equals=" + (deliveryNoteId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByJobSheetIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        JobSheet jobSheet;
        if (io.github.erp.web.rest.TestUtil.findAll(em, JobSheet.class).isEmpty()) {
            jobSheet = JobSheetResourceIT.createEntity(em);
            em.persist(jobSheet);
            em.flush();
        } else {
            jobSheet = io.github.erp.web.rest.TestUtil.findAll(em, JobSheet.class).get(0);
        }
        em.persist(jobSheet);
        em.flush();
        workInProgressRegistration.addJobSheet(jobSheet);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long jobSheetId = jobSheet.getId();

        // Get all the workInProgressRegistrationList where jobSheet equals to jobSheetId
        defaultWorkInProgressRegistrationShouldBeFound("jobSheetId.equals=" + jobSheetId);

        // Get all the workInProgressRegistrationList where jobSheet equals to (jobSheetId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("jobSheetId.equals=" + (jobSheetId + 1));
    }

    @Test
    @Transactional
    void getAllWorkInProgressRegistrationsByDealerIsEqualToSomething() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Dealer dealer;
        if (io.github.erp.web.rest.TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = io.github.erp.web.rest.TestUtil.findAll(em, Dealer.class).get(0);
        }
        em.persist(dealer);
        em.flush();
        workInProgressRegistration.setDealer(dealer);
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        Long dealerId = dealer.getId();

        // Get all the workInProgressRegistrationList where dealer equals to dealerId
        defaultWorkInProgressRegistrationShouldBeFound("dealerId.equals=" + dealerId);

        // Get all the workInProgressRegistrationList where dealer equals to (dealerId + 1)
        defaultWorkInProgressRegistrationShouldNotBeFound("dealerId.equals=" + (dealerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkInProgressRegistrationShouldBeFound(String filter) throws Exception {
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workInProgressRegistration.getId().intValue())))
            .andExpect(jsonPath("$.[*].sequenceNumber").value(hasItem(DEFAULT_SEQUENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].particulars").value(hasItem(DEFAULT_PARTICULARS)))
            .andExpect(jsonPath("$.[*].instalmentAmount").value(hasItem(sameNumber(DEFAULT_INSTALMENT_AMOUNT))))
            .andExpect(jsonPath("$.[*].commentsContentType").value(hasItem(DEFAULT_COMMENTS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(Base64Utils.encodeToString(DEFAULT_COMMENTS))));

        // Check, that the count call also returns 1
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkInProgressRegistrationShouldNotBeFound(String filter) throws Exception {
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWorkInProgressRegistration() throws Exception {
        // Get the workInProgressRegistration
        restWorkInProgressRegistrationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWorkInProgressRegistration() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();

        // Update the workInProgressRegistration
        WorkInProgressRegistration updatedWorkInProgressRegistration = workInProgressRegistrationRepository
            .findById(workInProgressRegistration.getId())
            .get();
        // Disconnect from session so that the updates on updatedWorkInProgressRegistration are not directly saved in db
        em.detach(updatedWorkInProgressRegistration);
        updatedWorkInProgressRegistration
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .particulars(UPDATED_PARTICULARS)
            .instalmentAmount(UPDATED_INSTALMENT_AMOUNT)
            .comments(UPDATED_COMMENTS)
            .commentsContentType(UPDATED_COMMENTS_CONTENT_TYPE);
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(
            updatedWorkInProgressRegistration
        );

        restWorkInProgressRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workInProgressRegistrationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isOk());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);
        WorkInProgressRegistration testWorkInProgressRegistration = workInProgressRegistrationList.get(
            workInProgressRegistrationList.size() - 1
        );
        assertThat(testWorkInProgressRegistration.getSequenceNumber()).isEqualTo(UPDATED_SEQUENCE_NUMBER);
        assertThat(testWorkInProgressRegistration.getParticulars()).isEqualTo(UPDATED_PARTICULARS);
        assertThat(testWorkInProgressRegistration.getInstalmentAmount()).isEqualByComparingTo(UPDATED_INSTALMENT_AMOUNT);
        assertThat(testWorkInProgressRegistration.getComments()).isEqualTo(UPDATED_COMMENTS);
        assertThat(testWorkInProgressRegistration.getCommentsContentType()).isEqualTo(UPDATED_COMMENTS_CONTENT_TYPE);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository).save(testWorkInProgressRegistration);
    }

    @Test
    @Transactional
    void putNonExistingWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();
        workInProgressRegistration.setId(count.incrementAndGet());

        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkInProgressRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workInProgressRegistrationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();
        workInProgressRegistration.setId(count.incrementAndGet());

        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInProgressRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();
        workInProgressRegistration.setId(count.incrementAndGet());

        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInProgressRegistrationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void partialUpdateWorkInProgressRegistrationWithPatch() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();

        // Update the workInProgressRegistration using partial update
        WorkInProgressRegistration partialUpdatedWorkInProgressRegistration = new WorkInProgressRegistration();
        partialUpdatedWorkInProgressRegistration.setId(workInProgressRegistration.getId());

        partialUpdatedWorkInProgressRegistration.sequenceNumber(UPDATED_SEQUENCE_NUMBER).particulars(UPDATED_PARTICULARS);

        restWorkInProgressRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkInProgressRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(partialUpdatedWorkInProgressRegistration))
            )
            .andExpect(status().isOk());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);
        WorkInProgressRegistration testWorkInProgressRegistration = workInProgressRegistrationList.get(
            workInProgressRegistrationList.size() - 1
        );
        assertThat(testWorkInProgressRegistration.getSequenceNumber()).isEqualTo(UPDATED_SEQUENCE_NUMBER);
        assertThat(testWorkInProgressRegistration.getParticulars()).isEqualTo(UPDATED_PARTICULARS);
        assertThat(testWorkInProgressRegistration.getInstalmentAmount()).isEqualByComparingTo(DEFAULT_INSTALMENT_AMOUNT);
        assertThat(testWorkInProgressRegistration.getComments()).isEqualTo(DEFAULT_COMMENTS);
        assertThat(testWorkInProgressRegistration.getCommentsContentType()).isEqualTo(DEFAULT_COMMENTS_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateWorkInProgressRegistrationWithPatch() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();

        // Update the workInProgressRegistration using partial update
        WorkInProgressRegistration partialUpdatedWorkInProgressRegistration = new WorkInProgressRegistration();
        partialUpdatedWorkInProgressRegistration.setId(workInProgressRegistration.getId());

        partialUpdatedWorkInProgressRegistration
            .sequenceNumber(UPDATED_SEQUENCE_NUMBER)
            .particulars(UPDATED_PARTICULARS)
            .instalmentAmount(UPDATED_INSTALMENT_AMOUNT)
            .comments(UPDATED_COMMENTS)
            .commentsContentType(UPDATED_COMMENTS_CONTENT_TYPE);

        restWorkInProgressRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkInProgressRegistration.getId())
                    .contentType("application/merge-patch+json")
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(partialUpdatedWorkInProgressRegistration))
            )
            .andExpect(status().isOk());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);
        WorkInProgressRegistration testWorkInProgressRegistration = workInProgressRegistrationList.get(
            workInProgressRegistrationList.size() - 1
        );
        assertThat(testWorkInProgressRegistration.getSequenceNumber()).isEqualTo(UPDATED_SEQUENCE_NUMBER);
        assertThat(testWorkInProgressRegistration.getParticulars()).isEqualTo(UPDATED_PARTICULARS);
        assertThat(testWorkInProgressRegistration.getInstalmentAmount()).isEqualByComparingTo(UPDATED_INSTALMENT_AMOUNT);
        assertThat(testWorkInProgressRegistration.getComments()).isEqualTo(UPDATED_COMMENTS);
        assertThat(testWorkInProgressRegistration.getCommentsContentType()).isEqualTo(UPDATED_COMMENTS_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();
        workInProgressRegistration.setId(count.incrementAndGet());

        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkInProgressRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workInProgressRegistrationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();
        workInProgressRegistration.setId(count.incrementAndGet());

        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInProgressRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkInProgressRegistration() throws Exception {
        int databaseSizeBeforeUpdate = workInProgressRegistrationRepository.findAll().size();
        workInProgressRegistration.setId(count.incrementAndGet());

        // Create the WorkInProgressRegistration
        WorkInProgressRegistrationDTO workInProgressRegistrationDTO = workInProgressRegistrationMapper.toDto(workInProgressRegistration);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInProgressRegistrationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(io.github.erp.web.rest.TestUtil.convertObjectToJsonBytes(workInProgressRegistrationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkInProgressRegistration in the database
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(0)).save(workInProgressRegistration);
    }

    @Test
    @Transactional
    void deleteWorkInProgressRegistration() throws Exception {
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);

        int databaseSizeBeforeDelete = workInProgressRegistrationRepository.findAll().size();

        // Delete the workInProgressRegistration
        restWorkInProgressRegistrationMockMvc
            .perform(delete(ENTITY_API_URL_ID, workInProgressRegistration.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkInProgressRegistration> workInProgressRegistrationList = workInProgressRegistrationRepository.findAll();
        assertThat(workInProgressRegistrationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WorkInProgressRegistration in Elasticsearch
        verify(mockWorkInProgressRegistrationSearchRepository, times(1)).deleteById(workInProgressRegistration.getId());
    }

    @Test
    @Transactional
    void searchWorkInProgressRegistration() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        workInProgressRegistrationRepository.saveAndFlush(workInProgressRegistration);
        when(mockWorkInProgressRegistrationSearchRepository.search("id:" + workInProgressRegistration.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(workInProgressRegistration), PageRequest.of(0, 1), 1));

        // Search the workInProgressRegistration
        restWorkInProgressRegistrationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + workInProgressRegistration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workInProgressRegistration.getId().intValue())))
            .andExpect(jsonPath("$.[*].sequenceNumber").value(hasItem(DEFAULT_SEQUENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].particulars").value(hasItem(DEFAULT_PARTICULARS)))
            .andExpect(jsonPath("$.[*].instalmentAmount").value(hasItem(sameNumber(DEFAULT_INSTALMENT_AMOUNT))))
            .andExpect(jsonPath("$.[*].commentsContentType").value(hasItem(DEFAULT_COMMENTS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(Base64Utils.encodeToString(DEFAULT_COMMENTS))));
    }
}

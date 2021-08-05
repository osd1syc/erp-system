package io.github.erp.web.rest;

import static io.github.erp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.erp.IntegrationTest;
import io.github.erp.domain.Payment;
import io.github.erp.domain.PaymentCalculation;
import io.github.erp.domain.PaymentCategory;
import io.github.erp.repository.PaymentCalculationRepository;
import io.github.erp.repository.search.PaymentCalculationSearchRepository;
import io.github.erp.service.criteria.PaymentCalculationCriteria;
import io.github.erp.service.dto.PaymentCalculationDTO;
import io.github.erp.service.mapper.PaymentCalculationMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
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

/**
 * Integration tests for the {@link PaymentCalculationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PaymentCalculationResourceIT {

    private static final BigDecimal DEFAULT_PAYMENT_EXPENSE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT_EXPENSE = new BigDecimal(2);
    private static final BigDecimal SMALLER_PAYMENT_EXPENSE = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_WITHHOLDING_VAT = new BigDecimal(1);
    private static final BigDecimal UPDATED_WITHHOLDING_VAT = new BigDecimal(2);
    private static final BigDecimal SMALLER_WITHHOLDING_VAT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_WITHHOLDING_TAX = new BigDecimal(1);
    private static final BigDecimal UPDATED_WITHHOLDING_TAX = new BigDecimal(2);
    private static final BigDecimal SMALLER_WITHHOLDING_TAX = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_PAYMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_PAYMENT_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/payment-calculations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/payment-calculations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentCalculationRepository paymentCalculationRepository;

    @Autowired
    private PaymentCalculationMapper paymentCalculationMapper;

    /**
     * This repository is mocked in the io.github.erp.repository.search test package.
     *
     * @see io.github.erp.repository.search.PaymentCalculationSearchRepositoryMockConfiguration
     */
    @Autowired
    private PaymentCalculationSearchRepository mockPaymentCalculationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentCalculationMockMvc;

    private PaymentCalculation paymentCalculation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentCalculation createEntity(EntityManager em) {
        PaymentCalculation paymentCalculation = new PaymentCalculation()
            .paymentExpense(DEFAULT_PAYMENT_EXPENSE)
            .withholdingVAT(DEFAULT_WITHHOLDING_VAT)
            .withholdingTax(DEFAULT_WITHHOLDING_TAX)
            .paymentAmount(DEFAULT_PAYMENT_AMOUNT);
        return paymentCalculation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentCalculation createUpdatedEntity(EntityManager em) {
        PaymentCalculation paymentCalculation = new PaymentCalculation()
            .paymentExpense(UPDATED_PAYMENT_EXPENSE)
            .withholdingVAT(UPDATED_WITHHOLDING_VAT)
            .withholdingTax(UPDATED_WITHHOLDING_TAX)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);
        return paymentCalculation;
    }

    @BeforeEach
    public void initTest() {
        paymentCalculation = createEntity(em);
    }

    @Test
    @Transactional
    void createPaymentCalculation() throws Exception {
        int databaseSizeBeforeCreate = paymentCalculationRepository.findAll().size();
        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);
        restPaymentCalculationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeCreate + 1);
        PaymentCalculation testPaymentCalculation = paymentCalculationList.get(paymentCalculationList.size() - 1);
        assertThat(testPaymentCalculation.getPaymentExpense()).isEqualByComparingTo(DEFAULT_PAYMENT_EXPENSE);
        assertThat(testPaymentCalculation.getWithholdingVAT()).isEqualByComparingTo(DEFAULT_WITHHOLDING_VAT);
        assertThat(testPaymentCalculation.getWithholdingTax()).isEqualByComparingTo(DEFAULT_WITHHOLDING_TAX);
        assertThat(testPaymentCalculation.getPaymentAmount()).isEqualByComparingTo(DEFAULT_PAYMENT_AMOUNT);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(1)).save(testPaymentCalculation);
    }

    @Test
    @Transactional
    void createPaymentCalculationWithExistingId() throws Exception {
        // Create the PaymentCalculation with an existing ID
        paymentCalculation.setId(1L);
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        int databaseSizeBeforeCreate = paymentCalculationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentCalculationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeCreate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void getAllPaymentCalculations() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentCalculation.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentExpense").value(hasItem(sameNumber(DEFAULT_PAYMENT_EXPENSE))))
            .andExpect(jsonPath("$.[*].withholdingVAT").value(hasItem(sameNumber(DEFAULT_WITHHOLDING_VAT))))
            .andExpect(jsonPath("$.[*].withholdingTax").value(hasItem(sameNumber(DEFAULT_WITHHOLDING_TAX))))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT))));
    }

    @Test
    @Transactional
    void getPaymentCalculation() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get the paymentCalculation
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentCalculation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentCalculation.getId().intValue()))
            .andExpect(jsonPath("$.paymentExpense").value(sameNumber(DEFAULT_PAYMENT_EXPENSE)))
            .andExpect(jsonPath("$.withholdingVAT").value(sameNumber(DEFAULT_WITHHOLDING_VAT)))
            .andExpect(jsonPath("$.withholdingTax").value(sameNumber(DEFAULT_WITHHOLDING_TAX)))
            .andExpect(jsonPath("$.paymentAmount").value(sameNumber(DEFAULT_PAYMENT_AMOUNT)));
    }

    @Test
    @Transactional
    void getPaymentCalculationsByIdFiltering() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        Long id = paymentCalculation.getId();

        defaultPaymentCalculationShouldBeFound("id.equals=" + id);
        defaultPaymentCalculationShouldNotBeFound("id.notEquals=" + id);

        defaultPaymentCalculationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPaymentCalculationShouldNotBeFound("id.greaterThan=" + id);

        defaultPaymentCalculationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPaymentCalculationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense equals to DEFAULT_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.equals=" + DEFAULT_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense equals to UPDATED_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.equals=" + UPDATED_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense not equals to DEFAULT_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.notEquals=" + DEFAULT_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense not equals to UPDATED_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.notEquals=" + UPDATED_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsInShouldWork() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense in DEFAULT_PAYMENT_EXPENSE or UPDATED_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.in=" + DEFAULT_PAYMENT_EXPENSE + "," + UPDATED_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense equals to UPDATED_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.in=" + UPDATED_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense is not null
        defaultPaymentCalculationShouldBeFound("paymentExpense.specified=true");

        // Get all the paymentCalculationList where paymentExpense is null
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense is greater than or equal to DEFAULT_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.greaterThanOrEqual=" + DEFAULT_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense is greater than or equal to UPDATED_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.greaterThanOrEqual=" + UPDATED_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense is less than or equal to DEFAULT_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.lessThanOrEqual=" + DEFAULT_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense is less than or equal to SMALLER_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.lessThanOrEqual=" + SMALLER_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsLessThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense is less than DEFAULT_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.lessThan=" + DEFAULT_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense is less than UPDATED_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.lessThan=" + UPDATED_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentExpenseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentExpense is greater than DEFAULT_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldNotBeFound("paymentExpense.greaterThan=" + DEFAULT_PAYMENT_EXPENSE);

        // Get all the paymentCalculationList where paymentExpense is greater than SMALLER_PAYMENT_EXPENSE
        defaultPaymentCalculationShouldBeFound("paymentExpense.greaterThan=" + SMALLER_PAYMENT_EXPENSE);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT equals to DEFAULT_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.equals=" + DEFAULT_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT equals to UPDATED_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.equals=" + UPDATED_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT not equals to DEFAULT_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.notEquals=" + DEFAULT_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT not equals to UPDATED_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.notEquals=" + UPDATED_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsInShouldWork() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT in DEFAULT_WITHHOLDING_VAT or UPDATED_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.in=" + DEFAULT_WITHHOLDING_VAT + "," + UPDATED_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT equals to UPDATED_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.in=" + UPDATED_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT is not null
        defaultPaymentCalculationShouldBeFound("withholdingVAT.specified=true");

        // Get all the paymentCalculationList where withholdingVAT is null
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT is greater than or equal to DEFAULT_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.greaterThanOrEqual=" + DEFAULT_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT is greater than or equal to UPDATED_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.greaterThanOrEqual=" + UPDATED_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT is less than or equal to DEFAULT_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.lessThanOrEqual=" + DEFAULT_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT is less than or equal to SMALLER_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.lessThanOrEqual=" + SMALLER_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsLessThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT is less than DEFAULT_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.lessThan=" + DEFAULT_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT is less than UPDATED_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.lessThan=" + UPDATED_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingVATIsGreaterThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingVAT is greater than DEFAULT_WITHHOLDING_VAT
        defaultPaymentCalculationShouldNotBeFound("withholdingVAT.greaterThan=" + DEFAULT_WITHHOLDING_VAT);

        // Get all the paymentCalculationList where withholdingVAT is greater than SMALLER_WITHHOLDING_VAT
        defaultPaymentCalculationShouldBeFound("withholdingVAT.greaterThan=" + SMALLER_WITHHOLDING_VAT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax equals to DEFAULT_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.equals=" + DEFAULT_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax equals to UPDATED_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.equals=" + UPDATED_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax not equals to DEFAULT_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.notEquals=" + DEFAULT_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax not equals to UPDATED_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.notEquals=" + UPDATED_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsInShouldWork() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax in DEFAULT_WITHHOLDING_TAX or UPDATED_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.in=" + DEFAULT_WITHHOLDING_TAX + "," + UPDATED_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax equals to UPDATED_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.in=" + UPDATED_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax is not null
        defaultPaymentCalculationShouldBeFound("withholdingTax.specified=true");

        // Get all the paymentCalculationList where withholdingTax is null
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax is greater than or equal to DEFAULT_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.greaterThanOrEqual=" + DEFAULT_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax is greater than or equal to UPDATED_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.greaterThanOrEqual=" + UPDATED_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax is less than or equal to DEFAULT_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.lessThanOrEqual=" + DEFAULT_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax is less than or equal to SMALLER_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.lessThanOrEqual=" + SMALLER_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsLessThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax is less than DEFAULT_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.lessThan=" + DEFAULT_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax is less than UPDATED_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.lessThan=" + UPDATED_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByWithholdingTaxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where withholdingTax is greater than DEFAULT_WITHHOLDING_TAX
        defaultPaymentCalculationShouldNotBeFound("withholdingTax.greaterThan=" + DEFAULT_WITHHOLDING_TAX);

        // Get all the paymentCalculationList where withholdingTax is greater than SMALLER_WITHHOLDING_TAX
        defaultPaymentCalculationShouldBeFound("withholdingTax.greaterThan=" + SMALLER_WITHHOLDING_TAX);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount equals to DEFAULT_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.equals=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount equals to UPDATED_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.equals=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount not equals to DEFAULT_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.notEquals=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount not equals to UPDATED_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.notEquals=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsInShouldWork() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount in DEFAULT_PAYMENT_AMOUNT or UPDATED_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.in=" + DEFAULT_PAYMENT_AMOUNT + "," + UPDATED_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount equals to UPDATED_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.in=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount is not null
        defaultPaymentCalculationShouldBeFound("paymentAmount.specified=true");

        // Get all the paymentCalculationList where paymentAmount is null
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount is greater than or equal to DEFAULT_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.greaterThanOrEqual=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount is greater than or equal to UPDATED_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.greaterThanOrEqual=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount is less than or equal to DEFAULT_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.lessThanOrEqual=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount is less than or equal to SMALLER_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.lessThanOrEqual=" + SMALLER_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount is less than DEFAULT_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.lessThan=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount is less than UPDATED_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.lessThan=" + UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        // Get all the paymentCalculationList where paymentAmount is greater than DEFAULT_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldNotBeFound("paymentAmount.greaterThan=" + DEFAULT_PAYMENT_AMOUNT);

        // Get all the paymentCalculationList where paymentAmount is greater than SMALLER_PAYMENT_AMOUNT
        defaultPaymentCalculationShouldBeFound("paymentAmount.greaterThan=" + SMALLER_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);
        Payment payment = PaymentResourceIT.createEntity(em);
        em.persist(payment);
        em.flush();
        paymentCalculation.setPayment(payment);
        payment.setPaymentCalculation(paymentCalculation);
        paymentCalculationRepository.saveAndFlush(paymentCalculation);
        Long paymentId = payment.getId();

        // Get all the paymentCalculationList where payment equals to paymentId
        defaultPaymentCalculationShouldBeFound("paymentId.equals=" + paymentId);

        // Get all the paymentCalculationList where payment equals to (paymentId + 1)
        defaultPaymentCalculationShouldNotBeFound("paymentId.equals=" + (paymentId + 1));
    }

    @Test
    @Transactional
    void getAllPaymentCalculationsByPaymentCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);
        PaymentCategory paymentCategory = PaymentCategoryResourceIT.createEntity(em);
        em.persist(paymentCategory);
        em.flush();
        paymentCalculation.setPaymentCategory(paymentCategory);
        paymentCalculationRepository.saveAndFlush(paymentCalculation);
        Long paymentCategoryId = paymentCategory.getId();

        // Get all the paymentCalculationList where paymentCategory equals to paymentCategoryId
        defaultPaymentCalculationShouldBeFound("paymentCategoryId.equals=" + paymentCategoryId);

        // Get all the paymentCalculationList where paymentCategory equals to (paymentCategoryId + 1)
        defaultPaymentCalculationShouldNotBeFound("paymentCategoryId.equals=" + (paymentCategoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentCalculationShouldBeFound(String filter) throws Exception {
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentCalculation.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentExpense").value(hasItem(sameNumber(DEFAULT_PAYMENT_EXPENSE))))
            .andExpect(jsonPath("$.[*].withholdingVAT").value(hasItem(sameNumber(DEFAULT_WITHHOLDING_VAT))))
            .andExpect(jsonPath("$.[*].withholdingTax").value(hasItem(sameNumber(DEFAULT_WITHHOLDING_TAX))))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT))));

        // Check, that the count call also returns 1
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentCalculationShouldNotBeFound(String filter) throws Exception {
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaymentCalculation() throws Exception {
        // Get the paymentCalculation
        restPaymentCalculationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPaymentCalculation() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();

        // Update the paymentCalculation
        PaymentCalculation updatedPaymentCalculation = paymentCalculationRepository.findById(paymentCalculation.getId()).get();
        // Disconnect from session so that the updates on updatedPaymentCalculation are not directly saved in db
        em.detach(updatedPaymentCalculation);
        updatedPaymentCalculation
            .paymentExpense(UPDATED_PAYMENT_EXPENSE)
            .withholdingVAT(UPDATED_WITHHOLDING_VAT)
            .withholdingTax(UPDATED_WITHHOLDING_TAX)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(updatedPaymentCalculation);

        restPaymentCalculationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentCalculationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);
        PaymentCalculation testPaymentCalculation = paymentCalculationList.get(paymentCalculationList.size() - 1);
        assertThat(testPaymentCalculation.getPaymentExpense()).isEqualTo(UPDATED_PAYMENT_EXPENSE);
        assertThat(testPaymentCalculation.getWithholdingVAT()).isEqualTo(UPDATED_WITHHOLDING_VAT);
        assertThat(testPaymentCalculation.getWithholdingTax()).isEqualTo(UPDATED_WITHHOLDING_TAX);
        assertThat(testPaymentCalculation.getPaymentAmount()).isEqualTo(UPDATED_PAYMENT_AMOUNT);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository).save(testPaymentCalculation);
    }

    @Test
    @Transactional
    void putNonExistingPaymentCalculation() throws Exception {
        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();
        paymentCalculation.setId(count.incrementAndGet());

        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentCalculationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentCalculationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentCalculation() throws Exception {
        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();
        paymentCalculation.setId(count.incrementAndGet());

        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentCalculationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentCalculation() throws Exception {
        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();
        paymentCalculation.setId(count.incrementAndGet());

        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentCalculationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void partialUpdatePaymentCalculationWithPatch() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();

        // Update the paymentCalculation using partial update
        PaymentCalculation partialUpdatedPaymentCalculation = new PaymentCalculation();
        partialUpdatedPaymentCalculation.setId(paymentCalculation.getId());

        partialUpdatedPaymentCalculation.paymentExpense(UPDATED_PAYMENT_EXPENSE);

        restPaymentCalculationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentCalculation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentCalculation))
            )
            .andExpect(status().isOk());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);
        PaymentCalculation testPaymentCalculation = paymentCalculationList.get(paymentCalculationList.size() - 1);
        assertThat(testPaymentCalculation.getPaymentExpense()).isEqualByComparingTo(UPDATED_PAYMENT_EXPENSE);
        assertThat(testPaymentCalculation.getWithholdingVAT()).isEqualByComparingTo(DEFAULT_WITHHOLDING_VAT);
        assertThat(testPaymentCalculation.getWithholdingTax()).isEqualByComparingTo(DEFAULT_WITHHOLDING_TAX);
        assertThat(testPaymentCalculation.getPaymentAmount()).isEqualByComparingTo(DEFAULT_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdatePaymentCalculationWithPatch() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();

        // Update the paymentCalculation using partial update
        PaymentCalculation partialUpdatedPaymentCalculation = new PaymentCalculation();
        partialUpdatedPaymentCalculation.setId(paymentCalculation.getId());

        partialUpdatedPaymentCalculation
            .paymentExpense(UPDATED_PAYMENT_EXPENSE)
            .withholdingVAT(UPDATED_WITHHOLDING_VAT)
            .withholdingTax(UPDATED_WITHHOLDING_TAX)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT);

        restPaymentCalculationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentCalculation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentCalculation))
            )
            .andExpect(status().isOk());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);
        PaymentCalculation testPaymentCalculation = paymentCalculationList.get(paymentCalculationList.size() - 1);
        assertThat(testPaymentCalculation.getPaymentExpense()).isEqualByComparingTo(UPDATED_PAYMENT_EXPENSE);
        assertThat(testPaymentCalculation.getWithholdingVAT()).isEqualByComparingTo(UPDATED_WITHHOLDING_VAT);
        assertThat(testPaymentCalculation.getWithholdingTax()).isEqualByComparingTo(UPDATED_WITHHOLDING_TAX);
        assertThat(testPaymentCalculation.getPaymentAmount()).isEqualByComparingTo(UPDATED_PAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingPaymentCalculation() throws Exception {
        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();
        paymentCalculation.setId(count.incrementAndGet());

        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentCalculationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentCalculationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentCalculation() throws Exception {
        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();
        paymentCalculation.setId(count.incrementAndGet());

        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentCalculationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentCalculation() throws Exception {
        int databaseSizeBeforeUpdate = paymentCalculationRepository.findAll().size();
        paymentCalculation.setId(count.incrementAndGet());

        // Create the PaymentCalculation
        PaymentCalculationDTO paymentCalculationDTO = paymentCalculationMapper.toDto(paymentCalculation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentCalculationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentCalculationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentCalculation in the database
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(0)).save(paymentCalculation);
    }

    @Test
    @Transactional
    void deletePaymentCalculation() throws Exception {
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);

        int databaseSizeBeforeDelete = paymentCalculationRepository.findAll().size();

        // Delete the paymentCalculation
        restPaymentCalculationMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentCalculation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PaymentCalculation> paymentCalculationList = paymentCalculationRepository.findAll();
        assertThat(paymentCalculationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PaymentCalculation in Elasticsearch
        verify(mockPaymentCalculationSearchRepository, times(1)).deleteById(paymentCalculation.getId());
    }

    @Test
    @Transactional
    void searchPaymentCalculation() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        paymentCalculationRepository.saveAndFlush(paymentCalculation);
        when(mockPaymentCalculationSearchRepository.search(queryStringQuery("id:" + paymentCalculation.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(paymentCalculation), PageRequest.of(0, 1), 1));

        // Search the paymentCalculation
        restPaymentCalculationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + paymentCalculation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentCalculation.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentExpense").value(hasItem(sameNumber(DEFAULT_PAYMENT_EXPENSE))))
            .andExpect(jsonPath("$.[*].withholdingVAT").value(hasItem(sameNumber(DEFAULT_WITHHOLDING_VAT))))
            .andExpect(jsonPath("$.[*].withholdingTax").value(hasItem(sameNumber(DEFAULT_WITHHOLDING_TAX))))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT))));
    }
}

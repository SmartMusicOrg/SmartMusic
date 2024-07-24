package com.example.smartmusicfirst.viewModels


import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ContactUsViewModelTest {
    private lateinit var contactUsViewModel: ContactUsViewModel

    @Before
    fun setUp() {
        contactUsViewModel = ContactUsViewModel()
    }

    @Test
    fun `updating subject should update the subject in the uiState`() {
        contactUsViewModel.updateSubject("Test Subject")
        assertThat(contactUsViewModel.uiState.value.emailSubject).isEqualTo("Test Subject")
    }

    @Test
    fun `updating body should update the body in the uiState`() {
        contactUsViewModel.updateBody("Test Body")
        assertThat(contactUsViewModel.uiState.value.emailBody).isEqualTo("Test Body")
    }
}
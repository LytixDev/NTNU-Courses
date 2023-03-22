describe("Tests if submit button is active", () => {
  beforeEach(() => {
      cy.visit("/Feedback");
  })

  it("Submit button is not active when no input has been typed in", () => {
      cy.get("#submit-button")
        .should("have.css", "opacity", "0.5");
  })

  it("Success on valid data being submitted", () => {
    cy.intercept("http://localhost:5000", {
        statusCode: 201,
    })

    cy.get("input[id='Name']").type("Nicolai Giga")
    cy.get("input[id='Email']").type("gigaNic@lol.c")
    cy.get("input[id='Message']").type("halla")
    cy.get("form").submit()
    cy.contains("#submit-message", "Skjemaet er sendt inn. Takk.")
  })

})
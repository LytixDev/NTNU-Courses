const runExpression = (...exprs) => {
  exprs.forEach((button_type) => {
      cy.contains("div", button_type).click();
  });
}

describe("Tests for running calculations", () => {
  beforeEach(() => {
      cy.visit("/");
  })

  it("Display shows result of an expression", () => {
      runExpression("1", "+", "2", "=");
      cy.get("#calculator-display").contains("3");
  })

  //it("Log contains expression typed and result", () => {
  //    runExpression("1", "+", "2", "=");
  //    cy.get("#log-box").contains("1 + 2 = 3");
  //})

  it("Delete button removes the last inputted type", () => {
      runExpression("1", "+", "2", "DEL");
      cy.get("#calculator-display").contains("1+");
  })
})
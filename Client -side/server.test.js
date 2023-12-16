const request = require("supertest");
const app = require("./server");

test("Server status Test", async () => {
  const response = await request(app).get("/");
  expect(response.statusCode).toBe(200);
});


test("GEt property Test", async () => {
    const response = await request(app).get("/result?search_query=mountainbike&page=1");
    expect(response.statusCode).toBe(200);
    expect(response.body).toHaveProperty("searchItem");
    expect(response.body).toHaveProperty("page");
    expect(response.body).toHaveProperty("results");
    expect(response.body).toHaveProperty("totalCount");

});

test("Get bikeName Test", async () => {
    // Make a GET request to "/productDetail?id=14" using the app
    const response = await request(app).get("/productDetail?id=14");

    // Assuming the response body is an object with a property 'bikeName'
    const result = response.body[0];

    // Expected bike name
    const expectedName = "Avalanche Comp ";

    // Use Jest's expect function to compare objects or properties
    expect(result.name).toEqual(expectedName);
});


test("GET result value Test", async () => {
    const response = await request(app).get("/result?search_query=mountainbike&page=1");
    expect(response.statusCode).toBe(200);
    let result = response.body; // Assuming the response body contains the object
    expect(result.page).toEqual("1");
});

test("Prices Test", async () => {
    const response = await request(app).get("/result?search_query=trek marlin 6");
    expect(response.statusCode).toBe(200);
    let result = response.body; // Assuming the response body contains the object
    expect(result.totalCount).toEqual(2);
});
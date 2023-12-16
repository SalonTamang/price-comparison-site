const expressServer = require("express");
const mysql = require("mysql");
const cors = require("cors");

//setting out our server
const app = expressServer();

// Serve static files from the "public" directory
app.use(expressServer.static("public"));

app.use(cors());

// setting ejs for rendering the html
// Set the view engine to EJS
app.set("view engine", "ejs");
app.set("views");

app.listen(3000, () => {
  console.log("listening on port number 3000");
});

const connection = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "",
  database: "cst3130_cw1",
  port: 3306,
});

app.get("/", (req, res) => {
  res.render("landingPage.ejs");
});

//get request directly from the index page
app.get("/results", (req, res) => {
  connection.getConnection((err, connection) => {
    if (err) {
      console.log("Error getting connection from pool: " + err.message);
      res.status(500).send("Internal Server Error");
      return;
    }

    const searchItem = req.query.search_query;
    const page = req.query.page || 1;
    const limit = 21;
    const offset = (page - 1) * limit;

    // Query for paginated results
    const queryResults = `SELECT a.id, a.name, a.brand, a.image, b.price,Count(b.price) AS total_price
      FROM bikes AS a
      JOIN prices AS b ON a.id = b.bike_id
      WHERE a.description LIKE '%${searchItem}%' OR a.name LIKE '%${searchItem}%'
      Group by a.id
      LIMIT ${limit} OFFSET ${offset};`;

    // Query for total count
    const queryTotalCount = `SELECT COUNT(*) AS total_count
      FROM bikes AS a
      JOIN prices AS b ON a.id = b.bike_id
      WHERE a.description LIKE '%${searchItem}%' OR a.name LIKE '%${searchItem}%';`;

    const queryBrand = `SELECT bikes.brand, COUNT(bikes.brand) AS brand_count
      FROM bikes
      JOIN prices ON bikes.id = prices.bike_id
      WHERE bikes.brand IN ('Specialized', 'Trek',"Orbea","Cannondale","Whyte","Gt Bikes","Pinnacle","Raleigh") -- Add other brands as needed
      GROUP BY bikes.brand;`;

    connection.query(queryResults, (errorResults, results, fields) => {
      if (errorResults) {
        console.log(
          "Error executing paginated statement: " + errorResults.message
        );
        res.status(500).send("Internal Server Error");
        return;
      }

      // Fetch total count
      connection.query(
        queryTotalCount,
        (errorCount, countResults, fieldsCount) => {
          if (errorCount) {
            console.log(
              "Error executing total count statement: " + errorCount.message
            );
            res.status(500).send("Internal Server Error");
            return;
          }
          const totalCount = countResults[0].total_count;

          connection.query(
            queryBrand,
            (errorBrand, brandResults, fieldsBrand) => {
              connection.release();
              if (errorBrand) {
                console.log(
                  "Error executing total count statement: " + errorBrand.message
                );
                res.status(500).send("Internal Server Error");
                return;
              }

              res.render("searchPage.ejs", {
                searchItem,
                page,
                results,
                totalCount,
                brandResults,
              });
            }
          );
        }
      );
    });
  });
});


//get request directly from the index page
app.get("/brand", (req, res) => {
  connection.getConnection((err, connection) => {
    if (err) {
      console.log("Error getting connection from pool: " + err.message);
      res.status(500).send("Internal Server Error");
      return;
    }

    const searchItem = req.query.q;
    const page = req.query.page || 1;
    const limit = 21;
    const offset = (page - 1) * limit;

    // Query for paginated results
    const queryResults = `SELECT a.id, a.name, a.brand, a.image, b.price,Count(b.price) AS total_price
      FROM bikes AS a
      JOIN prices AS b ON a.id = b.bike_id
      WHERE a.brand = '${searchItem}'
      Group by a.id
      LIMIT ${limit} OFFSET ${offset};`;

    // Query for total count
    const queryTotalCount = `SELECT COUNT(*) AS total_count
      FROM bikes AS a
      JOIN prices AS b ON a.id = b.bike_id
      WHERE a.brand = '${searchItem}';`;

    const queryBrand = `SELECT bikes.brand, COUNT(bikes.brand) AS brand_count
      FROM bikes
      JOIN prices ON bikes.id = prices.bike_id
      WHERE bikes.brand IN ('Specialized', 'Trek',"Orbea","Cannondale","Whyte","Gt Bikes","Pinnacle","Raleigh") -- Add other brands as needed
      GROUP BY bikes.brand;`;

    connection.query(queryResults, (errorResults, results, fields) => {
      if (errorResults) {
        console.log(
          "Error executing paginated statement: " + errorResults.message
        );
        res.status(500).send("Internal Server Error");
        return;
      }

      // Fetch total count
      connection.query(
        queryTotalCount,
        (errorCount, countResults, fieldsCount) => {
          if (errorCount) {
            console.log(
              "Error executing total count statement: " + errorCount.message
            );
            res.status(500).send("Internal Server Error");
            return;
          }
          const totalCount = countResults[0].total_count;

          connection.query(
            queryBrand,
            (errorBrand, brandResults, fieldsBrand) => {
              connection.release();
              if (errorBrand) {
                console.log(
                  "Error executing total count statement: " + errorBrand.message
                );
                res.status(500).send("Internal Server Error");
                return;
              }

              res.render("searchPage.ejs", {
                searchItem,
                page,
                results,
                totalCount,
                brandResults,
              });
            }
          );
        }
      );
    });
  });
});

app.get("/productDetails", (req, res) => {
  connection.getConnection((err, connection) => {
    if (err) {
      console.log("Error with connecting to dataBase: ", err.message);
      return;
    }

    const selectedItemId = req.query.id;

    const queryStatementFinalPage = `SELECT a.name,a.description, a.image,a.brand, b.price
    FROM bikes AS a
    JOIN prices AS b ON a.id = b.bike_id
    WHERE a.id = ${selectedItemId};`;

    //this is for the array of prices along side links to different websites
    const qr = `SELECT b.price as prices, b.url as urls, a.name, a.brand
    FROM bikes AS a
    JOIN prices AS b ON a.id = b.bike_id
    WHERE a.id = ${selectedItemId};`;

    connection.query(queryStatementFinalPage, (error, results) => {
      if (error) {
        console.log("Error executing statement: " + error.message);
        res.status(500).send("Internal Server Error");
        return;
      }

      connection.query(qr, (error, resultComparison) => {
        if (error) {
          console.log("Error executing statement: " + error.message);
          res.status(500).send("Internal Server Error");
          return;
        }
        res.render("comparisonPage.ejs", { results, resultComparison });
      });
    });
  });
});

//for Brands
app.get("/brands", (req, res) => {
  connection.getConnection((err, connection) => {
    if (err) {
      console.log("Error with connecting to dataBase: ", err.message);
      return;
    }

    const brand = req.query.q;

    const queryStatementFinalPage = `SELECT a.name,a.description, a.image,a.brand
    FROM bikes AS a
    JOIN prices AS b ON a.id = b.bike_id
    WHERE a.id = ${selectedItemId};`;

    //this is for the array of prices along side links to different websites
    const qr = `SELECT b.price as prices, b.url as urls
    FROM bikes AS a
    JOIN prices AS b ON a.id = b.bike_id
    WHERE a.id = ${selectedItemId};`;

    connection.query(queryStatementFinalPage, (error, results) => {
      if (error) {
        console.log("Error executing statement: " + error.message);
        res.status(500).send("Internal Server Error");
        return;
      }
    });
  });
});

//for testing
app.get("/productDetail", (req, res) => {
    const selectedItemId = req.query.id;
    const query = `SELECT bikes.name, bikes.description, bikes.image
    FROM bikes 
    WHERE bikes.id = ${selectedItemId};`;

    connection.query(query, (error, results) => {
      if (error) {
        console.log("Error executing statement: " + error.message);
        res.status(500).send("Internal Server Error");
        return;
      }

      res.send(results);
    });
  });


app.get("/result", (req, res) => {
  connection.getConnection((err, connection) => {
    if (err) {
      console.log("Error getting connection from pool: " + err.message);
      res.status(500).send("Internal Server Error");
      return;
    }

    const searchItem = req.query.search_query;
    const page = req.query.page || 1;
    const limit = 21;
    const offset = (page - 1) * limit;

    // Query for paginated results
    const queryResults = `SELECT a.id, a.name, a.brand, a.image, b.price,Count(b.price) AS total_price
      FROM bikes AS a
      JOIN prices AS b ON a.id = b.bike_id
      WHERE a.description LIKE '%${searchItem}%' OR a.name LIKE '%${searchItem}%'
      Group by a.id
      LIMIT ${limit} OFFSET ${offset};`;

    // Query for total count
    const queryTotalCount = `SELECT COUNT(*) AS total_count
      FROM bikes AS a
      JOIN prices AS b ON a.id = b.bike_id
      WHERE a.description LIKE '%${searchItem}%' OR a.name LIKE '%${searchItem}%';`;

    const queryTotalPrice = ``;
    connection.query(queryResults, (errorResults, results, fields) => {
      if (errorResults) {
        console.log(
          "Error executing paginated statement: " + errorResults.message
        );
        res.status(500).send("Internal Server Error");
        return;
      }

      // Fetch total count
      connection.query(
        queryTotalCount,
        (errorCount, countResults, fieldsCount) => {
          connection.release();

          if (errorCount) {
            console.log(
              "Error executing total count statement: " + errorCount.message
            );
            res.status(500).send("Internal Server Error");
            return;
          }

          const totalCount = countResults[0].total_count;

          res.send({ searchItem, page, results, totalCount });
        }
      );
    });
  });
});

module.exports = app;

A project for subscribing stock tickers and publishing updates to portfolio.

It takes a trade file of portfolio data, and use H2 database to simulate a mock market data provider.

This project uses Ant for building and Ivy for dependency management.

The driver class is portfolio.core.Portfolio

To run the program, use: ant run

The output report is called: portfolio-report.txt

Further improvements include:
1. Replace System.out.println with proper logging
2. Add unit tests
3. Using Spring Application Context for dependency injection

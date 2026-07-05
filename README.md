# Backend Akademija – ulazni zadatak

### Odabrane tehnologije:
- Java 21 
- Spring Boot 3.2.4 
- JPA/Hibernate
- Caffeine Cache za spremanje rezultata pretraga za bržu ponovnu uporabu
- H2 baza podataka u memoriji za pohranu korisničkih imena i lozinki

### Pokretanje aplikacije:
- aplikaciju pokrenuti pokretanjem _DemoApplication.java_

### Testiranje autentifikacije i autorizacije u Postmanu:
- potrebno je prvo poslati POST zahtjev na _/auth/login_ s korisničkim imenom i lozinkom u tijelu zahtjeva
  - **POST _localhost:8080/auth/login_**
  - Authorization opcija mora biti postavljena na **_No Auth_**
  - **Body (raw)** u formatu **JSON**:
    - za admina:
  
      {
       username: "testadmin",
       password: "ourpassword"
      }
    - za korisnika:

      {
      username: "testuser",
      password: "ourpassword"
      }
  - prekopirati token iz odgovora i u testiranju endpointova pod Authorization u Postmanu postaviti _Auth Type_ na _Bearer Token_ i u polje zalijepiti token
  - on omogućuje aplikaciji da prepozna radi li se o korisniku ili adminu
  - klase u paketu config i exception napisane su uz pomoć Gemini

### Endpointovi:

#### 1. _/products_
  - endpoint koji vraća listu proizvoda
  - ulazni podaci (Product klasa) definirani su prema formatu podataka na DummyJSON
  - izlazni podaci (ProductShort klasa) definirani su prema zadanom formatu (slika, naziv, cijena, skraćen opis do 100 znakova)
  - u svrhu prikaza autorizacije, pozivanje ovog endpointa omogućeno je samo adminu
  - testiranje u Postmanu:
    - poslati GET zahtjev na adresu _localhost:8080/products_
#### 2. _/product?id={}_
- endpoint koji vraća jedan proizvod prema idu
- ulazni podaci (Product klasa) definirani su prema formatu podataka na DummyJSON
- izlazni podaci ostavljeni su u orginalnom obliku
- endpoint može testirati i admin i user
- testiranje u Postmanu:
    - poslati GET zahtjev na adresu _localhost:8080/product?id={}_ gdje se u vitičastim zagradama upisuje id proizvoda koji želimo dohvatiti

#### 3. _/products/filter?lowerPrice={}&higherPrice={}&category={}_
- endpoint koji vraća listu proizvoda filtriranu prema zadanim parametrima
- kako bi se pojednostavila ugradnja, svi parametri nisu obavezni - može se filtrirati zasebno po svakom i u parovima
- ulazni podaci (Product klasa) definirani su prema formatu podataka na DummyJSON
- izlazni podaci ostavljeni su u orginalnom obliku
- endpoint može testirati i admin i user
- testiranje u Postmanu:
    - poslati GET zahtjev na adresu _localhost:8080/products/filter?lowerPrice={}&higherPrice={}&category={}_ gdje se u vitičastim zagradama upisuju minimalna i maksimalna cijena i kategorija proizvoda
    - npr. _localhost:8080/products/filter?lowerPrice=60&higherPrice=80&category=fragrances_

#### 4. _/products/search?searchText={}_
- endpoint koji vraća listu proizvoda filtriranu prema naslovu proizvoda na temelju upisanog teksta
- ukoliko se parametar ostavi prazan, prikazat će se lista svih proizvoda
- ulazni podaci (Product klasa) definirani su prema formatu podataka na DummyJSON
- izlazni podaci ostavljeni su u orginalnom obliku
- u svrhu prikaza autorizacije, pozivanje ovog endpointa omogućeno je samo useru
- testiranje u Postmanu:
  - poslati GET zahtjev na adresu _/products/search?searchText={}_ gdje se u vitičastim zagradama upisuje tekst na temelju kojeg želimo pronaći proizvod
  - npr. _localhost:8080/products/search?searchText=mas_


### Testiranje aplikacije s unit i integracijskim testovima:
- unit testovi za service nalaze se u paketu service, za repozitorij u paketu repo i controllere u paketu rest
- dva integracijska testa nalaze se u paketu integration
- testovi se pokreću s opcijom _Run_
- testovi su većinski generirani s Gemini i potom pregledani

Još neki primjeri za testiranje:

localhost:8080/products/filter?lowerPrice=60&category=fragrances
localhost:8080/products/filter?higherPrice=600&category=groceries - prazna lista
localhost:8080/product?id=15
localhost:8080/products/search?searchText=appl

Za pomoć u rješavanju zadatka korišten je Google Gemini, većinski u Pro Extended načinu rada. Korišten je kao pomoć u učenju novih koncepata (cacheing u Javi) i generiranje dijelova koda.
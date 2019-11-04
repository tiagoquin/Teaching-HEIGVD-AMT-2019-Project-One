function displayCountries() {
    const mainContainerElem = document.getElementById('main-container');

    if (tripsList.length < 1) {
      mainContainerElem.innerHTML =
        '<h1 class="title has-text-centered">No current trips</h1>\
        <h2 class="subtitle has-text-centered">Don\'t be shy, create a new trip with the green button</h2>';
      return;
    }
    let count = 1;
    let todisplay = tripsList.reduce((acc, obj) => {
      let cardDivs = `<div class="column is-3">
              <div class="card">
                <header class="card-header">
                  <p class="card-header-title">
                    ${countriesList[obj.idCountry - 1].name} 
                  </p>
                  <a href="#" class="card-header-icon" aria-label="more options">
                    <span class="icon">
                    </span>
                  </a>
                  <div class="card-header-icon">
                    <button class="button is-small is-danger is-outlined" onclick="deleteOfList(${
                      obj.idTrip
                    })">

                    <span class="icon is-small">
                      <i class="fas fa-times"></i>
                    </span>
                  </button>
                </header>
                <div class="card-content">
                  <div class="content">
                    <label class="label"> Date trip</label>

                    <input
                    id="input-date-${obj.idTrip}"
                    class="input" type="date"
                    onchange="editDate(${obj.idTrip})"
                    value=${obj.date} />
                  </div>
                  <div class="content">
                    <button class="button is-link ${
                      obj.visited === false ? 'is-outlined' : ''
                    }" onclick="toggleVisited(${obj.idTrip})">
                      <span class="icon is-small">
                        <i class="fas fa-check"></i>
                      </span>
                      <span>Visited</span>
                    </button>
                  </div>
                </div>
              </div>
              </div>`;
      let separator = '';
      if (count % 4 === 0) {
        separator = '</div><div class="columns is-desktop">';
      }
      count++;

      return acc + cardDivs + separator;
    }, '<div class="columns is-desktop">');
    mainContainerElem.innerHTML = todisplay;
  }

  displayCountries();

  function modalSwitch() {
    if (modalState) {
      modalState = false;
      modalelem.classList.remove('is-active');
      // htmlElem.classList.remove("is-clipped");
    } else {
      modalState = true;
      modalelem.classList.add('is-active');
      // htmlElem.classList.add("is-clipped");
    }
  }

  /* Adds a new country to list */
  async function addToList() {
    const e = document.getElementById('country-select');
    const countryChoice = e.options[e.selectedIndex].value;
    const dateChoice = document.getElementById('input-date').value;



    let params = "idCountry=" + countryChoice + "&date=" + dateChoice + "&visited=" + false;
    try {
      const resp = await axios({
        method: "POST",
        url: "home",
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        data : params
      });

    } catch (e) {
      console.error(e);
    }

    tripsList.unshift({
      "idTrip": 99,
      "idCountry": countryChoice,
      "date": dateChoice,
      "visited": false
    });

    console.log(tripsList);

    displayCountries();
    modalSwitch();


  }

  function deleteOfList(id) {
    tripsList = tripsList.filter(obj => obj.idTrip !== id);
    displayCountries();
  }

  function editDate(id) {
    const dateChoice = document.getElementById(`input-date-${id}`).value;

    tripsList.map(obj => {
      if (obj.idTrip === id) {
        obj.date = dateChoice;
      }
    });
    displayCountries();
  }

  function toggleVisited(id) {
    tripsList.map(obj => {
      if (obj.idTrip === id) {
        obj.visited = !obj.visited;
      }
    });
    displayCountries();
  }
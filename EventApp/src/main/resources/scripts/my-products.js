
$(document).ready(function() {
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        }
    });

    $('#editProductModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var locations = (button.data('locations') || "").toString().split(',');
        var eventTypes = (button.data('event-types') || "").toString().split(',');

        $('#edit-id').val(button.data('id'));
        $('#edit-name').val(button.data('name'));
        $('#edit-description').val(button.data('description'));
        $('#edit-price').val(button.data('price'));
        $('#edit-address').val(button.data('address'));
        $('#edit-phone').val(button.data('phone'));
        $('#edit-email').val(button.data('mail'));

        updateCheckboxGroup('#edit-locations', locations);
        updateCheckboxGroup('#edit-event-types', eventTypes);
    });

    function updateCheckboxGroup(selector, ids) {
        $(selector).find('input[type="checkbox"]').each(function() {
            $(this).prop('checked', ids.includes($(this).val()));
        });
    }

    $('#newProductForm').submit(function(event) {
        event.preventDefault();
        var formData = new FormData(this);

        $("input[name='categories']:checked").each(function() {
            formData.append('categoryIds', $(this).val());
        });

        $("input[name='subcategoryIds']:checked").each(function() {
            formData.append('subcategoryIds', $(this).val());
        });

        $("input[name='locations']:checked").each(function() {
            formData.append('locationIds', $(this).val());
        });

        $("input[name='eventTypes']:checked").each(function() {
            formData.append('eventTypeIds', $(this).val());
        });

        $('input[type="file"][name="photos"]').each(function() {
            var files = $(this).get(0).files;
            for (var i = 0; i < files.length; i++) {
                formData.append('photos', files[i]);
            }
        });

        for (var pair of formData.entries()) {
            console.log(pair[0]+ ', ' + pair[1]);
        }

        $.ajax({
            url: $(this).attr('action'),
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                addProductRow(response);
                $('#createProductModal').modal('hide');
                $('#newProductForm')[0].reset();
                console.log('Produs adăugat cu succes!', response);
            },
            error: function(xhr) {
                // Handle error
                console.error('Eroare la adăugarea produsului: ', xhr);
            }
        });
    });

    $('#editProductForm').submit(function(event) {
        event.preventDefault();
        var formData = new FormData(this);

        $("input[type='file'][name='photos']").each(function() {
            var files = $(this).get(0).files;
            for (var i = 0; i < files.length; i++) {
                formData.append('photos', files[i]);
            }
        });

        $.ajax({
            url: $(this).attr('action').replace('{id}', $('#edit-id').val()),
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                $('#editProductModal').modal('hide');
                updateProductRow(response);
                console.log('Produs actualizat cu succes!', response);
            },
            error: function(xhr) {
                // Handle error
                console.error('Eroare la actualizarea produsului: ', xhr);
            }
        });
    });
});

function addProductRow(product) {
    const tableBody = $('#productsTable tbody');
    const row = `<tr id="productRow${product.id}">
        <td>${product.name}</td>
        <td>${product.description}</td>
        <td>${product.price}</td>
        <td>${product.address}</td>
        <td>${product.phone}</td>
        <td>${product.mail}</td>
        <td>
            <button type="button" class="edit" data-toggle="modal" data-target="#editProductModal"
                data-id="${product.id}" data-name="${product.name}"
                data-description="${product.description}" data-price="${product.price}"
                data-address="${product.address}" data-phone="${product.phone}"
                data-mail="${product.mail}">Editare</button>
            <button type="button" class="delete" onclick="deleteProduct(${product.id})">Ștergere</button>
        </td>
        </tr>`;
    tableBody.append(row);
}

function updateProductRow(product) {
    var row = $('#productRow' + product.id);
    row.find('td:eq(0)').text(product.name);
    row.find('td:eq(1)').text(product.description);
    row.find('td:eq(2)').text(product.price);
    row.find('td:eq(3)').text(product.address);
    row.find('td:eq(4)').text(product.phone);
    row.find('td:eq(5)').text(product.mail);
}

function deleteProduct(productId) {
    var csrfToken = $('[name="_csrf"]').attr('content');
    var csrfHeader = $('[name="_csrf_header"]').attr('content');

    if (confirm("Sunteţi sigur că doriţi ştergerea produsului?")) {
        $.ajax({
            url: '/user/products/delete-product/' + productId,
            type: 'DELETE',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function () {
                alert('Produs şters cu succes!');
                $('#productRow' + productId).remove();
            },
            error: function (error) {
                alert('Eroare la ştergerea produsului: ' + error.responseText);
            }
        });
    }
}


function updateSubcategories() {
    const categoryCheckboxes = document.querySelectorAll('.category-checkbox:checked');
    const categoryIds = Array.from(categoryCheckboxes).map(cb => cb.getAttribute('data-category-id'));

    if (categoryIds.length > 0) {
        fetchSubcategories(categoryIds);
    } else {
        document.getElementById('new-subcategories').innerHTML = '';
    }
}

function fetchSubcategories(categoryIds) {
    const queryParams = categoryIds.map(id => `categoryIds=${encodeURIComponent(id)}`).join('&');
    const url = `/user/products/subcategories?${queryParams}`;
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP status ${response.status}`);
            }
            return response.json();
        })
        .then(subcategories => {
            console.log(subcategories);
            updateSubcategoriesDOM(subcategories);
        })
        .catch(error => {
            console.error('Eroare la încărcare subcategorii:', error);
        });
}

function updateSubcategoriesDOM(subcategories) {
    const container = document.getElementById('new-subcategories');
    container.innerHTML = '';

    subcategories.forEach(sub => {
        const checkboxContainer = document.createElement('div');
        checkboxContainer.className = 'checkbox-item';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.id = `new-subcategory-${sub.id}`;
        checkbox.name = 'subcategoryIds';
        checkbox.value = sub.id;

        const label = document.createElement('label');
        label.htmlFor = `new-subcategory-${sub.id}`;
        label.textContent = sub.name;

        checkboxContainer.appendChild(checkbox);
        checkboxContainer.appendChild(label);

        container.appendChild(checkboxContainer);
    });
}
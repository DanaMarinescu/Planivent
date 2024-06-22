$(document).ready(function() {
    var csrfToken = $('[name="_csrf"]').attr('content');
    var csrfHeader = $('[name="_csrf_header"]').attr('content');

    $('#calendar').fullCalendar({
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        },
        editable: true,
        selectable: true,
        selectHelper: true,
        eventLimit: true,
        eventRender: function (event, element) {
            element.find('.fc-title').append('<br/>' + moment(event.start).format('HH:mm'));
        },
        events: function (start, end, timezone, callback) {
            $.ajax({
                url: '/events/fetch',
                type: 'GET',
                dataType: 'json',
                data: {
                    start: start.format('YYYY-MM-DD'),
                    end: end.format('YYYY-MM-DD')
                },
                success: function (doc) {
                    var events = [];
                    $(doc).each(function () {
                        events.push({
                            id: this.id,
                            title: this.title,
                            start: moment.tz(this.dateTime, "Europe/Bucharest").format(),
                            description: this.description
                        });
                    });
                    callback(events);
                }
            });
        },
        select: function (start, end) {
            var dateFormat = 'YYYY-MM-DDTHH:mm:ssZ';
            var formattedStart = moment(start).tz("Europe/Bucharest").format(dateFormat);
            $('#eventDate').val(formattedStart);
            $('#eventModal').show();
        },
        eventClick: function (event) {
            if (confirm('Sunteţi sigur că ştergeţi evenimentul?')) {
                $.ajax({
                    url: '/events/delete/' + event.id,
                    type: 'DELETE',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken);
                    },
                    success: function () {
                        $('#calendar').fullCalendar('removeEvents', event.id);
                    }
                });
            }
        }
    });

    $('#eventForm').submit(function (e) {
        e.preventDefault();
        var eventTitle = $('#eventTitle').val();
        var eventDesc = $('#eventDesc').val();
        var eventDate = $('#eventDate').val();

        if (!eventDate) {
            alert("Data şi ora sunt obligatorii.");
            return;
        }

        var formattedDate = moment.tz(eventDate, "Europe/Bucharest").toISOString();
        addEventToDatabase(formattedDate, eventTitle, eventDesc);
    });

    function addEventToDatabase(date, title, description) {
        $.ajax({
            url: '/events/add',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                title: title,
                description: description,
                dateTime: date
            }),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                console.log("Eveniment adăugat cu succes!", data);
                $('#calendar').fullCalendar('refetchEvents');
                $('#eventModal').hide();
                $('#eventForm')[0].reset();
            },
            error: function (xhr) {
                console.error("Eroare la adăugare eveniment: " + xhr.responseText);
                alert("Adăugarea evenimentului a eşuat: " + xhr.responseText);
            }
        });
    }
});
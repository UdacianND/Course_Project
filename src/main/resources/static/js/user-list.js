$(document).ready(function(){


    $('[data-toggle="tooltip"]').tooltip();

    // Select/Deselect checkboxes
    let checkbox = $('table tbody input.table-checkbox');
    $("#selectAll").click(function(){
        if(this.checked){
            checkbox.each(function(){
                this.checked = true;
            });
        } else{
            checkbox.each(function(){
                this.checked = false;
            });
        }
    });
    checkbox.click(function(){
        if(!this.checked){
            $("#selectAll").prop("checked", false);
        }
    });


    let baseUrl = '/api/user/management/'
    function request(command){
        let selected = [];
        $('.table-checkbox:input:checked').each(function() {
            selected.push(parseInt($(this).attr('data-userid')));
        });

        $.ajax({
            url: baseUrl+command,
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(selected),
            async:false
        }).done(function(){
            window.location.href = '/'
        })

    }

    $("#delete").click(function(){
        request('delete')
    })
    $("#block").click(function(){
        request('block')
    })

    $("#unblock").click(function(){
        request('unblock')
    })


    $(".user_role").change(function(){
        console.log("checked")
        let userId = parseInt($(this).attr('data-userid'))
        let role = this.checked ? 'ADMIN':'USER'
        let userRoleDto = {
            'id':userId,
            'role':role
        }

        console.log(userRoleDto);
        $.ajax({
            url: '/api/user/management/user-role',
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(userRoleDto),
            async:false
        }).done(function(){
            window.location.href = '/'
        })
    })

});
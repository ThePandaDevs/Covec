const SUPABASE_URL = 'https://zafetnpukdsurpyhykzj.supabase.co'
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InphZmV0bnB1a2RzdXJweWh5a3pqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY0OTU2NjYwMSwiZXhwIjoxOTY1MTQyNjAxfQ.aBRLQAAxG4L4nxWINeNlFJZgozonsSJcqkxuRgKeY9Y'
const _supabase = supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY)

$(document).ready(function () {

    function uuidv4() {
        return ([1e9] + -43 + -8e3 + -11).replace(/[018]/g, c =>
            (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
        );
    }

    $("#botonSubir").click(async () => {
        var files = document.getElementById("imagenUpload");
        var nombre = $('#ncompleto').val();
        var telefono = $('#telefono').val();
        var email = $('#email').val();
        var password = $('#password').val();
        var passwordConfirmar = $('#passwordConfirmar').val();
        if (files.files[0] != null && nombre != "" && telefono != "" && email != "" && password != "" && passwordConfirmar != "") {

            if (password != passwordConfirmar) {
                new Notify({
                    status: 'warning',
                    title: 'Atención',
                    text: 'Las contraseñas no coinciden, revisalas nuevamente',
                    effect: 'fade',
                    speed: 300,
                    customClass: '',
                    customIcon: '',
                    showIcon: true,
                    showCloseButton: true,
                    autoclose: false,
                    autotimeout: 3000,
                    gap: 20,
                    distance: 20,
                    type: 1,
                    position: 'right top'
                })
            } else {
                const { data, error } = await _supabase.storage
                    .from('covec')
                    .upload(`public/${uuidv4()}`, files.files[0])

                if (data != null) {
                    $('#link').val(data.Key);

                } else if (error != null) {
                    new Notify({
                        status: 'error',
                        title: 'Atención',
                        text: 'Ocurrio un error al subir la imagen al servidor',
                        effect: 'fade',
                        speed: 300,
                        customClass: '',
                        customIcon: '',
                        showIcon: true,
                        showCloseButton: true,
                        autoclose: false,
                        autotimeout: 3000,
                        gap: 20,
                        distance: 20,
                        type: 1,
                        position: 'bottom x-center'
                    })
                }

                $("#subir").click();
            }
        } else {
            new Notify({
                status: 'warning',
                title: 'Atención',
                text: 'Es necesario llenar todos los campos',
                effect: 'fade',
                speed: 300,
                customClass: '',
                customIcon: '',
                showIcon: true,
                showCloseButton: true,
                autoclose: false,
                autotimeout: 3000,
                gap: 20,
                distance: 20,
                type: 1,
                position: 'right top'
            })
        }
    });
});
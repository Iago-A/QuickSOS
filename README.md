# QuickSOS

## 📝 Sobre la aplicación

QuickSOS es una aplicación para móviles Android, desarrollada como proyecto de fin de ciclo y diseñada para unificar en un solo lugar los principales teléfonos de emergencia y servicios de información a nivel nacional. Aunque el número 112 es ampliamente conocido, en muchas ocasiones los usuarios necesitan contactar con servicios específicos (como información médica, atención veterinaria o el centro de salud asignado) cuya localización y marcación puede resultar poco ágil en situaciones de tensión o urgencia relativa.

La aplicación ha sido desarrollada utilizando Android como plataforma principal, con Firebase como backend para la gestión de datos. Está pensada para cualquier usuario con un dispositivo Android y conexión a internet. Entre sus funciones principales se incluye la marcación directa de números de emergencia desde la propia app y la posibilidad de añadir contactos personalizados de interés para cada usuario.

En cuanto al ámbito lingüístico, actualmente ofrece soporte en español e inglés, y se prevé una fácil ampliación a otros idiomas como portugués, francés o italiano. A largo plazo, también se contempla la incorporación de teléfonos de emergencia de otros países, lo que abriría la posibilidad de uso a nivel internacional.

## :iphone: Funcionalidades de la aplicación
- `Inicio de sesión`: Los usuarios pueden iniciar sesión con su cuenta para poder ver todos los contactos predeterminados y personales guardados en la base de datos de Firebase. Una vez iniciada sesión, no habrá que volver a hacerlo si se cierra la app mientras no se cierre la sesión por el propio usuario.
  
- `Registrar cuenta nueva`: Si existe un inicio de sesión, por supuesto se tiene que poder registrar un nuevo usuario. Correo electrónico como contraseña (encriptada) son guardados en Firebase.
  
- `Visualizar contactos predeterminados`: Una de las dos funciones principales de la app. Se muestra el listado de los principales números de emergencia o contacto a nivel nacional (como el número de la Guardia Civíl, el Instituto de toxicología, el de IMSERSO...) Es almacenado en Firebase, y es fijo y común para todos los usuarios.
  
- `Visualizar contactos personales`: La segunda funcionalidad más importante de la app. Si la lista principal no contiene algún contacto por ser de carácter más local o cualquier otro motivo, aquí cada usuario puede disponer de distintos contactos propios que solo podrá ver él (como el número del centro de salud designado, número de un hospital veterinario de urgencias local...)  
De forma muy sencilla se puede:
  - Agregar un nuevo contacto.
  - Eliminar contacto si fue creado previamente.
    
- `Configuración`: Este apartado abarca distintas funciones útiles:
  - Cambiar de contraseña.
  - Cerrar sesión.
  - Eliminar cuenta.

- `Soporte de lenguajes`: La propia app cambiará de Español a Inglés dependiendo de cual sea el lenguaje definido del propio smartphone.  

## :computer: Estado del proyecto

<h4>
:construction: Proyecto en construcción :construction:
</h4>

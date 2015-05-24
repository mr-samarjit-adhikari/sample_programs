#include <qglobal.h>
#include <qapplication.h>

#include "discoveryUi.h"

int main( int argc, char ** argv )
{
    QApplication app( argc, argv );
    sunsetSolarDiscoveryUi discoUiForm;
    discoUiForm.show();
    app.connect( &app, SIGNAL( lastWindowClosed() ), &app, SLOT( quit() ) );
    return app.exec();
}


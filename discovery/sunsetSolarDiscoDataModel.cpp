///////////////////////////////////////////////////////////////////////
// $Author: rhs041163 $
// // $Id: sunsetSolarDiscoDataModel.cpp,v 1.4 2012/11/27 08:49:17 rhs041163 Exp $
// // $Date: 2012/11/27 08:49:17 $
// // $Revision: 1.4 $
// ///////////////////////////////////////////////////////////////////////

#include <iostream>

#include "sunsetSolarDiscoDataModel.h"

sunsetSolarDiscoDataObj::sunsetSolarDiscoDataObj(string &name,string &value)
{
    this->name = name;
    this->value = value;
}

sunsetSolarDiscoDataObj::sunsetSolarDiscoDataObj(char* name,char* value)
{
    this->name  = string(name);
    this->value = string(value);
}

sunsetSolarDiscoDataObj::~sunsetSolarDiscoDataObj()
{
    //Empty
}

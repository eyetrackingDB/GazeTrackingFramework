#include "stdafx.h"

#include <random>

//from https://bitbucket.org/Leszek/pupil-tracker/
static std::mt19937 static_gen;
int random(int min, int max)
{
    std::uniform_int_distribution<> distribution(min, max);
    return distribution(static_gen);
}

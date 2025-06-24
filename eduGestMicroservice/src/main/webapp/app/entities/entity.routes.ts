import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'student',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceStudent.home.title' },
    loadChildren: () => import('./eduGestMicroservice/student/student.routes'),
  },
  {
    path: 'teacher',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceTeacher.home.title' },
    loadChildren: () => import('./eduGestMicroservice/teacher/teacher.routes'),
  },
  {
    path: 'parent',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceParent.home.title' },
    loadChildren: () => import('./eduGestMicroservice/parent/parent.routes'),
  },
  {
    path: 'administration',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceAdministration.home.title' },
    loadChildren: () => import('./eduGestMicroservice/administration/administration.routes'),
  },
  {
    path: 'subject',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceSubject.home.title' },
    loadChildren: () => import('./eduGestMicroservice/subject/subject.routes'),
  },
  {
    path: 'school-class',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceSchoolClass.home.title' },
    loadChildren: () => import('./eduGestMicroservice/school-class/school-class.routes'),
  },
  {
    path: 'room',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceRoom.home.title' },
    loadChildren: () => import('./eduGestMicroservice/room/room.routes'),
  },
  {
    path: 'session',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceSession.home.title' },
    loadChildren: () => import('./eduGestMicroservice/session/session.routes'),
  },
  {
    path: 'timetable',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceTimetable.home.title' },
    loadChildren: () => import('./eduGestMicroservice/timetable/timetable.routes'),
  },
  {
    path: 'absence',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceAbsence.home.title' },
    loadChildren: () => import('./eduGestMicroservice/absence/absence.routes'),
  },
  {
    path: 'grades',
    data: { pageTitle: 'eduGestMicroserviceApp.eduGestMicroserviceGrades.home.title' },
    loadChildren: () => import('./eduGestMicroservice/grades/grades.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;

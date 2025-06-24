import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'eduGestGatewayApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'student',
    data: { pageTitle: 'eduGestGatewayApp.student.home.title' },
    loadChildren: () => import('./student/student.routes'),
  },
  {
    path: 'teacher',
    data: { pageTitle: 'eduGestGatewayApp.teacher.home.title' },
    loadChildren: () => import('./teacher/teacher.routes'),
  },
  {
    path: 'parent',
    data: { pageTitle: 'eduGestGatewayApp.parent.home.title' },
    loadChildren: () => import('./parent/parent.routes'),
  },
  {
    path: 'administration',
    data: { pageTitle: 'eduGestGatewayApp.administration.home.title' },
    loadChildren: () => import('./administration/administration.routes'),
  },
  {
    path: 'subject',
    data: { pageTitle: 'eduGestGatewayApp.subject.home.title' },
    loadChildren: () => import('./subject/subject.routes'),
  },
  {
    path: 'school-class',
    data: { pageTitle: 'eduGestGatewayApp.schoolClass.home.title' },
    loadChildren: () => import('./school-class/school-class.routes'),
  },
  {
    path: 'room',
    data: { pageTitle: 'eduGestGatewayApp.room.home.title' },
    loadChildren: () => import('./room/room.routes'),
  },
  {
    path: 'session',
    data: { pageTitle: 'eduGestGatewayApp.session.home.title' },
    loadChildren: () => import('./session/session.routes'),
  },
  {
    path: 'timetable',
    data: { pageTitle: 'eduGestGatewayApp.timetable.home.title' },
    loadChildren: () => import('./timetable/timetable.routes'),
  },
  {
    path: 'absence',
    data: { pageTitle: 'eduGestGatewayApp.absence.home.title' },
    loadChildren: () => import('./absence/absence.routes'),
  },
  {
    path: 'grades',
    data: { pageTitle: 'eduGestGatewayApp.grades.home.title' },
    loadChildren: () => import('./grades/grades.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
